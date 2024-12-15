package com.lox.authservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lox.authservice.models.Authtoken;
import com.lox.authservice.models.Credential;
import com.lox.authservice.models.responses.LoxAuthentication;
import com.lox.authservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Credential> signup(@RequestParam String username,
            @RequestParam String password) {
        try {
            Credential credential = authService.signup(username, password);
            return (ResponseEntity<Credential>) ResponseEntity.internalServerError();
        } catch (JsonProcessingException e) {
            log.error("Error during signup", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoxAuthentication> login(@RequestParam String username,
            @RequestParam String password) {
        try {
            return authService.generateToken(username, password)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(401).body(new LoxAuthentication()));
        } catch (JsonProcessingException e) {
            log.error("Error during login", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Authtoken> validate(@RequestHeader("Sectoken") String token) {
        return authService.validateToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(null));
    }
}

