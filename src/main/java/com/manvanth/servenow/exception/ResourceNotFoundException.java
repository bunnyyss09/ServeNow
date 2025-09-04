package com.manvanth.servenow.exception;

/**
 * Custom exception for resource not found scenarios
 * Thrown when requested resources (users, roles, etc.) are not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
    }
}