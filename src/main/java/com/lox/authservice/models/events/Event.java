// src/main/java/com/lox/authservice/models/events/Event.java

package com.lox.authservice.models.events;

import java.time.Instant;
import java.util.UUID;

public interface Event {
    String getEventType();
    UUID getUserId();
    Instant getTimestamp();
}
