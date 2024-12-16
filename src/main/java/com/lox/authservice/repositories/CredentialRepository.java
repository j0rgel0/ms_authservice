// src/main/java/com/lox/authservice/repositories/CredentialRepository.java

package com.lox.authservice.repositories;

import com.lox.authservice.models.Credential;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CredentialRepository extends ReactiveCrudRepository<Credential, UUID> {
    Mono<Credential> findByUserId(UUID userId);
}
