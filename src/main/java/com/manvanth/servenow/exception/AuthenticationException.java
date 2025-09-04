package com.manvanth.servenow.exception;

/**
 * Custom exception for authentication failures
 * Thrown when authentication operations fail
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}