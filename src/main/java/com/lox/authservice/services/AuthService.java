// src/main/java/com/lox/authservice/services/AuthService.java

package com.lox.authservice.services;

import com.lox.authservice.exceptions.EmailAlreadyExistsException;
import com.lox.authservice.exceptions.UsernameAlreadyExistsException;
import com.lox.authservice.kafka.Producer;
import com.lox.authservice.models.AuthToken;
import com.lox.authservice.models.Credential;
import com.lox.authservice.models.User;
import com.lox.authservice.models.events.AuthEvent;
import com.lox.authservice.repositories.AuthTokenRepository;
import com.lox.authservice.repositories.CredentialRepository;
import com.lox.authservice.repositories.UserRepository;
import com.lox.authservice.util.JWTUtil;
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
    private final Producer producer;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<User> registerUser(User user, String rawPassword) {
        log.info("Attempting to register user: {}", user.getUsername());

        return userRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> {
                    log.warn("Registration failed: Username {} already exists", user.getUsername());
                    return Mono.error(new UsernameAlreadyExistsException("Username already exists"));
                })
                .switchIfEmpty(
                        userRepository.findByEmail(user.getEmail())
                                .flatMap(existingUser -> {
                                    log.warn("Registration failed: Email {} already exists", user.getEmail());
                                    return Mono.error(new EmailAlreadyExistsException("Email already exists"));
                                })
                )
                .switchIfEmpty(
                        Mono.defer(() -> {
                            user.setCreatedAt(Instant.now());
                            log.info("Saving new user: {}", user.getUsername());
                            return userRepository.save(user);
                        })
                )
                .flatMap(savedUser -> {
                    if (!(savedUser instanceof User userObj)) {
                        return Mono.error(new RuntimeException("Saved user is not an instance of User"));
                    }

                    Credential credential = Credential.builder()
                            .userId(userObj.getId())
                            .password(passwordEncoder.encode(rawPassword))
                            .build();
                    log.info("Saving credentials for user: {}", userObj.getUsername());
                    return credentialRepository.save(credential)
                            .thenReturn(userObj);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(savedUser -> {
                    log.info("User registered successfully: {}", savedUser.getUsername());
                    AuthEvent event = AuthEvent.builder()
                            .eventType("USER_CREATED")
                            .userId(savedUser.getId())
                            .username(savedUser.getUsername())
                            .email(savedUser.getEmail())
                            .fullName(savedUser.getFullName())
                            .timestamp(Instant.now())
                            .build();
                    producer.publishAuthEvent(event);
                })
                .doOnError(error -> log.error("Error during user registration: {}", error.getMessage()));
    }

    public Mono<String> authenticate(String email, String rawPassword, String ipAddress,
            String userAgent, String referer) {
        log.info("Attempting to authenticate user: {}", email);

        return userRepository.findByEmail(email)
                .flatMap(user -> credentialRepository.findByUserId(user.getId())
                        .switchIfEmpty(Mono.error(new RuntimeException("Invalid email or password")))
                        .flatMap(credential -> {
                            if (passwordEncoder.matches(rawPassword, credential.getPassword())) {
                                String role = "ROLE_USER";
                                String token = jwtUtil.generateToken(String.valueOf(user.getId()), role);
                                Instant now = Instant.now();
                                Instant expiry = now.plusMillis(jwtUtil.getJwtExpirationMs());

                                AuthToken authToken = AuthToken.builder()
                                        .token(token)
                                        .userId(user.getId())
                                        .creationTime(now)
                                        .expiryTime(expiry)
                                        .build();

                                log.info("Authentication successful for user: {}", email);
                                return authTokenRepository.save(authToken)
                                        .thenReturn(token);
                            } else {
                                log.warn("Authentication failed: Invalid password for user {}", email);
                                return Mono.error(new RuntimeException("Invalid email or password"));
                            }
                        }))
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(token -> {
                    // En este punto, el usuario ya ha sido autenticado y el token ha sido generado
                    // Para incluir el userId en el evento, necesitamos acceder al usuario
                    // Sin embargo, en este contexto solo tenemos acceso al token
                    // Por lo tanto, es mejor emitir el evento dentro del flatMap anterior
                    // Alternativamente, se puede utilizar un mecanismo de contexto o refactorizar el método
                    log.info("Authentication process completed for user: {}", email);
                })
                .doOnError(error -> {
                    log.error("Authentication error for user {}: {}", email, error.getMessage());
                    AuthEvent event = AuthEvent.builder()
                            .eventType("LOGIN_FAILED")
                            .userId(null) // Puede ser null si el usuario no existe
                            .username(email)
                            .email(null)
                            .fullName(null)
                            .timestamp(Instant.now())
                            .ipAddress(ipAddress)
                            .userAgent(userAgent)
                            .referer(referer)
                            .failureReason(error.getMessage())
                            .build();
                    producer.publishAuthEvent(event);
                })
                .flatMap(token -> {
                    // Necesitamos obtener el userId para el evento de autenticación exitosa
                    // Una forma de hacerlo es buscar nuevamente al usuario por email
                    // Sin embargo, esto introduce una consulta adicional
                    // En lugar de eso, vamos a modificar el flujo para capturar el userId
                    // Refactorizaremos el método para emitir el evento dentro del flujo
                    return userRepository.findByEmail(email)
                            .flatMap(user -> {
                                AuthEvent event = AuthEvent.builder()
                                        .eventType("USER_AUTHENTICATED")
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .fullName(user.getFullName())
                                        .timestamp(Instant.now())
                                        .ipAddress(ipAddress)
                                        .userAgent(userAgent)
                                        .referer(referer)
                                        .build();
                                producer.publishAuthEvent(event);
                                return Mono.just(token);
                            });
                });
    }
}
