package com.lox.authservice.models.events;

import com.lox.authservice.kafka.EventType;
import com.lox.authservice.models.User;

import java.time.Instant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthEventBuilder {

    public static AuthEvent userCreated(User user) {
        return AuthEvent.builder()
                .eventType(EventType.USER_CREATED.name())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .timestamp(Instant.now())
                .build();
    }

    public static AuthEvent userAuthenticated(User user, String ipAddress, String userAgent, String referer) {
        return AuthEvent.builder()
                .eventType(EventType.USER_AUTHENTICATED.name())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .timestamp(Instant.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referer(referer)
                .build();
    }

    public static AuthEvent loginFailed(String email, String ipAddress, String userAgent, String referer, String failureReason) {
        return AuthEvent.builder()
                .eventType(EventType.LOGIN_FAILED.name())
                .userId(null)
                .username(email)
                .email(null)
                .fullName(null)
                .timestamp(Instant.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referer(referer)
                .failureReason(failureReason)
                .build();
    }
}
