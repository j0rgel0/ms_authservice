package com.lox.authservice.models.events;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEvent {

    private String eventType; // "USER_CREATED", "USER_AUTHENTICATED", "LOGIN_FAILED", etc.
    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private Instant timestamp;
    private String ipAddress;
    private String failureReason;
    private String userAgent;
    private String referer;
}
