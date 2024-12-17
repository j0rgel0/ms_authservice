package com.lox.authservice.api.repositories;

import com.lox.authservice.api.models.AuthToken;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthTokenRepository extends ReactiveCrudRepository<AuthToken, UUID> {

    Mono<AuthToken> findByToken(String token);
}
