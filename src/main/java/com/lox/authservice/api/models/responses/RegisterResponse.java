// src/main/java/com/lox/authservice/models/responses/RegisterResponse.java

package com.lox.authservice.api.models.responses;

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
public class RegisterResponse {

    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private Instant createdAt;
}
