package com.lox.authservice.models.responses;

import com.lox.authservice.models.Authtoken;
import lombok.Data;

@Data
public class LoxAuthentication {

    private boolean authenticated;
    private String message;
    private Authtoken authtoken;
}
