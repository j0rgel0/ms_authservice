package com.lox.authservice.security;

import com.lox.authservice.security.util.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        List<SimpleGrantedAuthority> authorities = jwtUtil.getAuthorities(token);

        if (username != null && jwtUtil.validateToken(token)) {
            Authentication auth = new UsernamePasswordAuthenticationToken(username, token,
                    authorities);
            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }
}
