package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Home controller for basic API endpoints
 * Provides health check and application information
 */
@RestController
@RequestMapping("/")
@Slf4j
@Tag(name = "Application", description = "Basic application endpoints")
public class HomeController {

    /**
     * API health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "application", "ServeNow Local Service Finder",
            "version", "1.0.0"
        );
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
            "Application is running", healthInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * API information endpoint
     */
    @GetMapping("/info")
    @Operation(summary = "Application info", description = "Get application information")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> appInfo = Map.of(
            "name", "ServeNow",
            "description", "Local Service Finder Platform",
            "version", "1.0.0",
            "apiVersion", "v1",
            "documentation", "/swagger-ui.html",
            "contact", Map.of(
                "developer", "Manvanth",
                "email", "admin@servenow.com"
            )
        );
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(appInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Welcome message for root endpoint
     */
    @GetMapping
    @Operation(summary = "Welcome message", description = "Get welcome message")
    public ResponseEntity<ApiResponse<Map<String, String>>> welcome() {
        Map<String, String> welcome = Map.of(
            "message", "Welcome to ServeNow API",
            "description", "Local Service Finder Platform",
            "documentation", "/swagger-ui.html"
        );
        
        ApiResponse<Map<String, String>> response = ApiResponse.success(
            "Welcome to ServeNow", welcome);
        
        return ResponseEntity.ok(response);
    }
}
