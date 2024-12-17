package com.lox.authservice.api.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
