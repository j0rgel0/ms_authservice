// src/main/java/com/lox/authservice/models/Credential.java

package com.lox.authservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("credentials")
public class Credential {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    private String password;

    // Otros campos y métodos según sea necesario
}
