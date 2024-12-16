// src/main/java/com/lox/authservice/exceptions/EmailAlreadyExistsException.java

package com.lox.authservice.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
