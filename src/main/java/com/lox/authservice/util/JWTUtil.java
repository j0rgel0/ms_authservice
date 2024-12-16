package com.lox.authservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", role)
                .withIssuedAt(new java.util.Date())
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(algorithm);
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            // Invalid token
            return false;
        }
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        String roles = decodedJWT.getClaim("roles").asString();
        return List.of(new SimpleGrantedAuthority(roles));
    }

}
