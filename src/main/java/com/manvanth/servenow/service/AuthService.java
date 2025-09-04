package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.request.LoginRequest;
import com.manvanth.servenow.dto.request.RegisterRequest;
import com.manvanth.servenow.dto.response.AuthResponse;
import com.manvanth.servenow.dto.response.UserResponse;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.exception.AuthenticationException;
import com.manvanth.servenow.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations
 * Handles user login, registration, and JWT token management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    /**
     * Authenticate user and generate JWT tokens
     */
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            log.info("User authenticated successfully: {}", user.getEmail());

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Get token expiration
            Long expiresIn = jwtService.getAccessTokenExpiration();

            // Convert user to response DTO
            UserResponse userResponse = userMapper.toUserResponse(user);

            return new AuthResponse(accessToken, refreshToken, expiresIn, userResponse);

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email: {} - Invalid credentials", loginRequest.getEmail());
            throw new AuthenticationException("Invalid email or password");
        } catch (DisabledException e) {
            log.warn("Failed login attempt for email: {} - Account disabled", loginRequest.getEmail());
            throw new AuthenticationException("Account is disabled");
        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            throw new AuthenticationException("Authentication failed");
        }
    }

    /**
     * Register new user and generate JWT tokens
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());

        // Register user
        UserResponse userResponse = userService.registerUser(registerRequest);

        // Get user entity for token generation
        User user = userService.findUserEntityById(userResponse.getId());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Get token expiration
        Long expiresIn = jwtService.getAccessTokenExpiration();

        log.info("User registered and authenticated successfully: {}", user.getEmail());

        return new AuthResponse(accessToken, refreshToken, expiresIn, userResponse);
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");

        try {
            // Validate refresh token
            if (!jwtService.isTokenValid(refreshToken)) {
                throw new AuthenticationException("Invalid refresh token");
            }

            // Extract user from token
            String email = jwtService.extractUsername(refreshToken);
            User user = userService.findUserEntityByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            // Verify token belongs to user
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new AuthenticationException("Refresh token does not belong to user");
            }

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(user);
            Long expiresIn = jwtService.getAccessTokenExpiration();

            // Convert user to response DTO
            UserResponse userResponse = userMapper.toUserResponse(user);

            log.info("Access token refreshed successfully for user: {}", user.getEmail());

            return new AuthResponse(newAccessToken, refreshToken, expiresIn, userResponse);

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new AuthenticationException("Token refresh failed");
        }
    }

    /**
     * Logout user (invalidate tokens on client side)
     * Note: JWT tokens are stateless, so server-side logout requires token blacklisting
     * For now, we log the logout event
     */
    public void logout(String userEmail) {
        log.info("User logged out: {}", userEmail);
        // In a production system, you might want to:
        // 1. Add token to blacklist
        // 2. Store logout event
        // 3. Send notification
    }

    /**
     * Validate access token and return user information
     */
    @Transactional(readOnly = true)
    public UserResponse validateToken(String token) {
        try {
            if (!jwtService.isTokenValid(token)) {
                throw new AuthenticationException("Invalid token");
            }

            String email = jwtService.extractUsername(token);
            User user = userService.findUserEntityByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            if (!jwtService.isTokenValid(token, user)) {
                throw new AuthenticationException("Token validation failed");
            }

            return userMapper.toUserResponse(user);

        } catch (Exception e) {
            log.error("Token validation failed", e);
            throw new AuthenticationException("Token validation failed");
        }
    }
}