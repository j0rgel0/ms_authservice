package com.lox.authservice.api.kafka.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthEvent implements Event {

    @JsonProperty("eventType")
    private final String eventType; // "USER_CREATED", "USER_AUTHENTICATED", "LOGIN_FAILED", etc.

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
}
