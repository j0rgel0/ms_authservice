package com.lox.authservice.api.repositories;

import com.lox.authservice.api.models.Credential;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CredentialRepository extends ReactiveCrudRepository<Credential, UUID> {

    Mono<Credential> findByUserId(UUID userId);
}
