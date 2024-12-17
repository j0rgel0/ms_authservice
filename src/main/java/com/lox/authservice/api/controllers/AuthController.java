package com.lox.authservice.api.controllers;

import com.lox.authservice.api.models.User;
import com.lox.authservice.api.models.requests.LoginRequest;
import com.lox.authservice.api.models.requests.RegisterRequest;
import com.lox.authservice.api.services.AuthService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> registerUser(
            @Valid @RequestBody RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .createdAt(Instant.now())
                .build();

        String password = registerRequest.getPassword();

        return authService.registerUser(user, password)
                .map(ResponseEntity::ok)
                .onErrorResume(Mono::error);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@Valid @RequestBody LoginRequest loginRequest,
            ServerHttpRequest request) {
        String ipAddress =
                request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress()
                        .getHostAddress() : "UNKNOWN";
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        String referer = request.getHeaders().getFirst(HttpHeaders.REFERER);

        return authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword(),
                        ipAddress, userAgent, referer)
                .map(token -> ResponseEntity.ok(token))
                .onErrorResume(
                        e -> Mono.just(ResponseEntity.status(401).body("Invalid credentials")));
    }

}
