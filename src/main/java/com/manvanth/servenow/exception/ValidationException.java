package com.manvanth.servenow.exception;

/**
 * Custom exception for validation errors
 * Thrown when business validation rules are violated
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}