// src/main/java/com/lox/authservice/models/responses/CreateUserResponse.java

package com.lox.authservice.api.models.responses;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Instant createdAt;
}
