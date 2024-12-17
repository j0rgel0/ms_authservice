package com.lox.authservice.api.models;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("authtokens")
public class AuthToken {

    @Id
    private UUID id;

    private String token;

    @Column("user_id")
    private UUID userId;

    @Column("creation_time")
    private Instant creationTime;

    @Column("expiry_time")
    private Instant expiryTime;

}
