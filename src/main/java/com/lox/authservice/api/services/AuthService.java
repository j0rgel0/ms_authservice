package com.lox.authservice.api.services;

import com.lox.authservice.api.exceptions.EmailAlreadyExistsException;
import com.lox.authservice.api.exceptions.UsernameAlreadyExistsException;
import com.lox.authservice.api.kafka.events.AuthEvent;
import com.lox.authservice.api.kafka.events.EventType;
import com.lox.authservice.api.models.AuthToken;
import com.lox.authservice.api.models.Credential;
import com.lox.authservice.api.models.User;
import com.lox.authservice.api.repositories.AuthTokenRepository;
import com.lox.authservice.api.repositories.CredentialRepository;
import com.lox.authservice.api.repositories.UserRepository;
import com.lox.authservice.common.kafka.event.EventProducer;
import com.lox.authservice.security.util.JWTUtil;
import java.time.Instant;
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
                .flatMap(existingUser -> Mono.<User>error(
                        new UsernameAlreadyExistsException("The username already exists")))
                .switchIfEmpty(
                        userRepository.findByEmail(user.getEmail())
                                .flatMap(existingUser -> Mono.<User>error(
                                        new EmailAlreadyExistsException(
                                                "The email already exists")))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    user.setCreatedAt(Instant.now());
                    log.info("Saving new user: {}", user.getUsername());
                    return userRepository.save(user)
                            .doOnSuccess(savedUser -> log.info("User saved: {}",
                                    savedUser.getUsername()));
                }))
                .flatMap(savedUser -> {
                    Credential credential = Credential.builder()
                            .userId(savedUser.getId())
                            .password(passwordEncoder.encode(rawPassword))
                            .build();
                    log.info("Saving credentials for user: {}", savedUser.getUsername());
                    return credentialRepository.save(credential)
                            .thenReturn(savedUser);
                })
                .publishOn(Schedulers.boundedElastic())
                .flatMap(savedUser -> {
                    log.info("User successfully registered: {}", savedUser.getUsername());
                    AuthEvent event = AuthEvent.userCreated(savedUser);
                    return sendEvent(EventType.USER_CREATED, event)
                            .thenReturn(savedUser);
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

                                log.info("Successful authentication for user: {}", email);
                                return authTokenRepository.save(authToken)
                                        .thenReturn(user); // Save auth token and return user.
                            } else {
                                log.warn("Authentication failed: Invalid password for user {}",
                                        email);
                                return Mono.error(new RuntimeException(
                                        "Invalid email or password")); // Invalid password.
                            }
                        }))
                .flatMap(user -> {
                    AuthEvent event = AuthEvent.userAuthenticated(user, ipAddress, userAgent,
                            referer);
                    return sendEvent(EventType.USER_AUTHENTICATED, event)
                            .thenReturn(jwtUtil.generateToken(String.valueOf(user.getId()),
                                    "ROLE_USER")); // Return JWT.
                })
                .doOnSuccess(
                        token -> log.info("Authentication process completed for user: {}", email))
                .publishOn(
                        Schedulers.boundedElastic()) // Switch to an elastic scheduler for heavy tasks.
                .doOnError(error -> {
                    log.error("Authentication error for user {}: {}", email, error.getMessage());
                    AuthEvent event = AuthEvent.loginFailed(email, ipAddress, userAgent, referer,
                            error.getMessage()); // Build login failure event.
                    sendEvent(EventType.LOGIN_FAILED, event).subscribe(); // Send failure event.
                });
    }

    private <T extends AuthEvent> Mono<Void> sendEvent(EventType eventType, T event) {
        log.info("Sending event of type {}: {}", eventType, event);
        return producer.publishEvent(eventType.getTopic(), event)
                .doOnSuccess(aVoid -> log.info("Event {} sent successfully", eventType))
                .doOnError(error -> log.error("Error sending event {}: {}", eventType,
                        error.getMessage()));
    }
}
