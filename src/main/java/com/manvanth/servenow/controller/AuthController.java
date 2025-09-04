package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.request.LoginRequest;
import com.manvanth.servenow.dto.request.RegisterRequest;
import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.AuthResponse;
import com.manvanth.servenow.dto.response.UserResponse;
import com.manvanth.servenow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations
 * Handles user registration, login, token refresh, and logout
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with customer or provider role")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for email: {}", registerRequest.getEmail());
        
        AuthResponse authResponse = authService.register(registerRequest);
        
        ApiResponse<AuthResponse> response = ApiResponse.success(
            "User registered successfully", authResponse);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        
        AuthResponse authResponse = authService.login(loginRequest);
        
        ApiResponse<AuthResponse> response = ApiResponse.success(
            "Login successful", authResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token refresh request received");
        
        // Extract token from Bearer header
        String refreshToken = extractTokenFromHeader(authHeader);
        
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        
        ApiResponse<AuthResponse> response = ApiResponse.success(
            "Token refreshed successfully", authResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate access token
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token and return user information")
    public ResponseEntity<ApiResponse<UserResponse>> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token validation request received");
        
        // Extract token from Bearer header
        String token = extractTokenFromHeader(authHeader);
        
        UserResponse userResponse = authService.validateToken(token);
        
        ApiResponse<UserResponse> response = ApiResponse.success(
            "Token is valid", userResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client should discard tokens)")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        // Extract user email from request attributes (set by JWT filter)
        String userEmail = (String) request.getAttribute("userEmail");
        
        if (userEmail != null) {
            authService.logout(userEmail);
            log.info("User logged out: {}", userEmail);
        }
        
        ApiResponse<Object> response = ApiResponse.success(
            "Logged out successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to extract token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        return authHeader.substring(7);
    }
}