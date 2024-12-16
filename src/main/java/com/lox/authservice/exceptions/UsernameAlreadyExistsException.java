// src/main/java/com/lox/authservice/exceptions/UsernameAlreadyExistsException.java

package com.lox.authservice.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
