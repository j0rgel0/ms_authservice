// src/main/java/com/lox/authservice/models/User.java

package com.lox.authservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User {

    @Id
    private UUID id;

    private String username;
    private String email;
    private String fullName;
    private Instant createdAt;

    // Otros campos y métodos según sea necesario
}
