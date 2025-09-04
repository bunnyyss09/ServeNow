package com.manvanth.servenow.exception;

import com.manvanth.servenow.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Global exception handler for consistent error responses
 * Handles all exceptions and converts them to standardized API responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>error(
            "Validation failed", HttpStatus.BAD_REQUEST.value())
            .withPath(request.getDescription(false));
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle constraint validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>error(
            "Validation failed", HttpStatus.BAD_REQUEST.value())
            .withPath(request.getDescription(false));
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value())
                .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle user-related exceptions
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(
            UserException ex, WebRequest request) {
        
        log.warn("User error: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
                .withPath(request.getDescription(false));
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle custom validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Business validation error: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
                .withPath(request.getDescription(false));
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, com.manvanth.servenow.exception.AuthenticationException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            Exception ex, WebRequest request) {
        
        log.warn("Authentication error: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Authentication failed: " + ex.getMessage(), 
            HttpStatus.UNAUTHORIZED.value())
            .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle bad credentials exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        log.warn("Bad credentials: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Invalid email or password", 
            HttpStatus.UNAUTHORIZED.value())
            .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle access denied exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Access denied: " + ex.getMessage(), 
            HttpStatus.FORBIDDEN.value())
            .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
                .withPath(request.getDescription(false));
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value())
                .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        ApiResponse<Object> response = ApiResponse.error(
            "An unexpected error occurred. Please try again later.", 
            HttpStatus.INTERNAL_SERVER_ERROR.value())
            .withPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}