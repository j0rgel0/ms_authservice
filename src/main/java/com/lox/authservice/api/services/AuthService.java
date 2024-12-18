package com.lox.authservice.api.services;

import com.lox.authservice.api.exceptions.EmailAlreadyExistsException;
import com.lox.authservice.api.exceptions.UsernameAlreadyExistsException;
import com.lox.authservice.api.kafka.events.EventProducer;
import com.lox.authservice.api.kafka.events.AuthEventBuilder;
import com.lox.authservice.api.models.Credential;
import com.lox.authservice.api.models.User;
import com.lox.authservice.api.repositories.AuthTokenRepository;
import com.lox.authservice.api.repositories.CredentialRepository;
import com.lox.authservice.api.repositories.UserRepository;
import com.lox.authservice.api.kafka.events.EventType;
import com.lox.authservice.api.models.AuthToken;
import com.lox.authservice.api.kafka.events.AuthEvent;
import com.lox.authservice.api.kafka.events.Event;
import com.lox.authservice.security.util.JWTUtil;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JWTUtil jwtUtil;
    private final EventProducer producer;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<User> registerUser(User user, String rawPassword) {
        log.info("Attempting to register user: {}", user.getUsername());

        return userRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> {
                    return Mono.<User>error(
                            new UsernameAlreadyExistsException("Username already exists"));
                }) // Check if the username already exists.
                .switchIfEmpty(
                        userRepository.findByEmail(user.getEmail())
                                .flatMap(existingUser -> {
                                    return Mono.<User>error(new EmailAlreadyExistsException(
                                            "Email already exists"));
                                }) // Check if the email already exists.
                )
                .switchIfEmpty(Mono.defer(() -> {
                    user.setCreatedAt(Instant.now()); // Set creation time.
                    log.info("Saving new user: {}", user.getUsername());
                    return userRepository.save(user).log("save-user"); // Save the new user.
                }))
                .flatMap(savedUser -> {
                    Credential credential = Credential.builder()
                            .userId(savedUser.getId())
                            .password(
                                    passwordEncoder.encode(rawPassword)) // Encode the raw password.
                            .build();
                    log.info("Saving credentials for user: {}", savedUser.getUsername());
                    return credentialRepository.save(credential)
                            .thenReturn(savedUser); // Save credentials and return the user.
                })
                .publishOn(
                        Schedulers.boundedElastic()) // Switch to a bounded elastic scheduler for heavy tasks.
                .flatMap(savedUser -> {
                    log.info("User registered successfully: {}", savedUser.getUsername());
                    AuthEvent event = AuthEventBuilder.userCreated(
                            savedUser); // Build a "user created" event.
                    CompletableFuture<Void> eventFuture = sendEvent(EventType.USER_CREATED,
                            event).toFuture();
                    return Mono.fromFuture(eventFuture)
                            .thenReturn(savedUser); // Send event and return the user.
                })
                .doOnError(error -> log.error("Error during user registration: {}",
                        error.getMessage())); // Log registration errors.
    }

    public Mono<String> authenticate(String email, String rawPassword, String ipAddress,
            String userAgent, String referer) {
        log.info("Attempting to authenticate user: {}", email);

        return userRepository.findByEmail(email)
                .flatMap(user -> credentialRepository.findByUserId(user.getId())
                        .switchIfEmpty(Mono.error(new RuntimeException(
                                "Invalid email or password"))) // Handle invalid credentials.
                        .flatMap(credential -> {
                            if (passwordEncoder.matches(rawPassword, credential.getPassword())) {
                                String role = "ROLE_USER";
                                String token = jwtUtil.generateToken(String.valueOf(user.getId()),
                                        role); // Generate JWT.
                                Instant now = Instant.now();
                                Instant expiry = now.plusMillis(
                                        jwtUtil.getJwtExpirationMs()); // Calculate token expiry time.

                                AuthToken authToken = AuthToken.builder()
                                        .token(token)
                                        .userId(user.getId())
                                        .creationTime(now)
                                        .expiryTime(expiry)
                                        .build();

                                log.info("Authentication successful for user: {}", email);
                                return authTokenRepository.save(authToken)
                                        .thenReturn(
                                                user); // Save the auth token and return the user.
                            } else {
                                log.warn("Authentication failed: Invalid password for user {}",
                                        email);
                                return Mono.error(new RuntimeException(
                                        "Invalid email or password")); // Invalid password.
                            }
                        }))
                .flatMap(user -> {
                    AuthEvent event = AuthEventBuilder.userAuthenticated(user, ipAddress, userAgent,
                            referer); // Build an authentication event.
                    CompletableFuture<Void> eventFuture = sendEvent(EventType.USER_AUTHENTICATED,
                            event).toFuture();
                    return Mono.fromFuture(eventFuture)
                            .then(Mono.just(jwtUtil.generateToken(String.valueOf(user.getId()),
                                    "ROLE_USER"))); // Return the JWT.
                })
                .doOnSuccess(
                        token -> log.info("Authentication process completed for user: {}", email))
                .publishOn(
                        Schedulers.boundedElastic()) // Switch to a bounded elastic scheduler for heavy tasks.
                .doOnError(error -> {
                    log.error("Authentication error for user {}: {}", email, error.getMessage());
                    AuthEvent event = AuthEventBuilder.loginFailed(email, ipAddress, userAgent,
                            referer, error.getMessage()); // Build a login failure event.
                    sendEvent(EventType.LOGIN_FAILED, event).subscribe(); // Send failure event.
                });
    }

    private <T extends Event> Mono<Void> sendEvent(EventType eventType, T event) {
        log.info("Sending event of type {}: {}", eventType, event); // Log event sending.
        return producer.publishEvent(eventType, event)
                .doOnSuccess(aVoid -> log.info("Event {} sent successfully", eventType)) // Log success.
                .doOnError(error -> log.error("Error sending event {}: {}", eventType, error.getMessage())); // Log errors.
    }
}
