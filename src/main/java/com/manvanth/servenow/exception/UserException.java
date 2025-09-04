package com.manvanth.servenow.exception;

/**
 * Custom exception for user-related operations
 * Thrown when user operations fail due to business logic violations
 */
public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}