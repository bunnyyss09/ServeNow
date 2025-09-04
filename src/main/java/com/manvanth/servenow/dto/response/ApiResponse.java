package com.manvanth.servenow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper
 * Provides consistent response format across all endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private Integer statusCode;

    // Success response constructors
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, LocalDateTime.now(), null, 200);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null, 200);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now(), null, 200);
    }

    // Error response constructors
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null, 400);
    }

    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null, statusCode);
    }

    public static <T> ApiResponse<T> error(String message, String path, Integer statusCode) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), path, statusCode);
    }

    // Builder pattern methods
    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    public ApiResponse<T> withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}