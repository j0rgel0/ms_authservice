package com.lox.authservice.models;

import java.time.Instant;
import lombok.Data;

@Data
public class Analytic {

    private String objectid;
    private String type;
    private String principal;
    private Instant timestamp;
    private String description;

}