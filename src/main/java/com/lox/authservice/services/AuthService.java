package com.lox.authservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lox.authservice.kafka.Producer;
import com.lox.authservice.models.*;
import com.lox.authservice.models.responses.LoxAuthentication;
import com.lox.authservice.repositories.*;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialRepository credentialRepository;
    private final AuthtokenRepository authtokenRepository;
    private final Producer producer;

    public Credential signup(String username, String password) throws JsonProcessingException {
        Credential credential = new Credential();
        credential.setUsername(username);
        credential.setPassword(password);
        credentialRepository.save(credential);

        producer.publishAuthDatum(username, "NEW SIGNUP");

        credential.setPassword("*********"); // Mask password for response
        return credential;
    }

    public Optional<LoxAuthentication> generateToken(String username, String password) throws JsonProcessingException {
        Credential credential = credentialRepository.findById(username).orElse(null);

        LoxAuthentication loxAuth = new LoxAuthentication();

        if (credential != null && credential.getPassword().equals(password)) {
            Authtoken token = new Authtoken();
            token.setUsername(username);
            token.setToken(String.valueOf((int) (Math.random() * 1000000)));
            token.setCreationtime(Instant.now());
            token.setExpirytime(300); // Expiry in seconds
            authtokenRepository.save(token);

            producer.publishAuthDatum(username, "LOGIN SUCCESSFUL");

            loxAuth.setAuthenticated(true);
            loxAuth.setMessage("LOGIN SUCCESSFUL");
            loxAuth.setAuthtoken(token);

            return Optional.of(loxAuth);
        }

        producer.publishAuthDatum(username, "LOGIN UNSUCCESSFUL");
        loxAuth.setAuthenticated(false);
        loxAuth.setMessage("INVALID CREDENTIALS");
        return Optional.of(loxAuth);
    }

    public Optional<Authtoken> validateToken(String token) {
        return authtokenRepository.findById(token);
    }
}
