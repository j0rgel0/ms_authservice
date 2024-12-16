package com.lox.authservice.repositories;

import com.lox.authservice.models.Credential;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CredentialRepository extends ReactiveCrudRepository<Credential, UUID> {

    Mono<Credential> findByUserId(UUID userId);
}
