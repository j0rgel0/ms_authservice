package com.lox.authservice.common.kafka.event;

import java.time.Instant;
import java.util.UUID;

public interface Event {
    String getEventType();
    UUID getUserId();
    Instant getTimestamp();
}
