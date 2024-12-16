// src/main/java/com/lox/authservice/repositories/UserRepository.java

package com.lox.authservice.repositories;

import com.lox.authservice.models.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);
}
