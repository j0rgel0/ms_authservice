// src/main/java/com/lox/authservice/repositories/AuthTokenRepository.java

package com.lox.authservice.repositories;

import com.lox.authservice.models.AuthToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthTokenRepository extends ReactiveCrudRepository<AuthToken, UUID> {
    Mono<AuthToken> findByToken(String token);
}
