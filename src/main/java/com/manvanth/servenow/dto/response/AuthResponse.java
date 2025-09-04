package com.manvanth.servenow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for authentication responses
 * Contains JWT token and user information after successful login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Token expiration time in seconds
    private UserResponse user;
    private LocalDateTime loginTime;

    // Constructor for successful authentication
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.loginTime = LocalDateTime.now();
    }

    // Constructor without refresh token
    public AuthResponse(String accessToken, Long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.loginTime = LocalDateTime.now();
    }
}