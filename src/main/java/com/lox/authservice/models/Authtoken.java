package com.lox.authservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;

@Data
@Entity
@Table(name = "authtokens")
public class Authtoken {

    @Id
    @Column(name = "token", nullable = false, length = 50)
    private String token;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "creationtime")
    private Instant creationtime;

    @Column(name = "expirytime", nullable = false)
    private Integer expirytime;

}