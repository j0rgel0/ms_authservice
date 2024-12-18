package com.lox.authservice.api.kafka.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lox.authservice.api.models.User;
import com.lox.authservice.common.kafka.event.Event;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthEvent implements Event {

    @JsonProperty("eventType")
    private final String eventType;

    @JsonProperty("userId")
    private final UUID userId;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("fullName")
    private final String fullName;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant timestamp;

    @JsonProperty("ipAddress")
    private final String ipAddress;

    @JsonProperty("userAgent")
    private final String userAgent;

    @JsonProperty("referer")
    private final String referer;

    @JsonProperty("failureReason")
    private final String failureReason;

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    // Static methods to build events

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

    public static AuthEvent userAuthenticated(User user, String ipAddress, String userAgent,
            String referer) {
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

    public static AuthEvent loginFailed(String email, String ipAddress, String userAgent,
            String referer, String failureReason) {
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
