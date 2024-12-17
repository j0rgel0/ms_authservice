package com.lox.authservice.api.repositories;

import com.lox.authservice.api.models.User;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);
}
