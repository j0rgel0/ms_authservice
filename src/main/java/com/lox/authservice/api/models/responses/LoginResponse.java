// src/main/java/com/lox/authservice/models/responses/LoginResponse.java

package com.lox.authservice.api.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
}
