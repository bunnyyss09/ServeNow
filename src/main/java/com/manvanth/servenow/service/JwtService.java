package com.manvanth.servenow.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for JWT token operations
 * Handles token generation, validation, and claims extraction
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate access token for user
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        // Add user roles to claims
        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        extraClaims.put("roles", roles);
        extraClaims.put("type", "access");

        return generateToken(extraClaims, userDetails, accessTokenExpiration);
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("type", "refresh");
        
        return generateToken(extraClaims, userDetails, refreshTokenExpiration);
    }

    /**
     * Generate JWT token with extra claims
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Validate JWT token
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Validate JWT token without user details
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * Check if JWT token is expired
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract user roles from JWT token
     */
    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", String.class);
    }

    /**
     * Extract token type from JWT token
     */
    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * Check if token is access token
     */
    public boolean isAccessToken(String token) {
        return "access".equals(extractTokenType(token));
    }

    /**
     * Check if token is refresh token
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    /**
     * Get access token expiration time in seconds
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000; // Convert to seconds
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration / 1000; // Convert to seconds
    }

    /**
     * Get remaining time until token expires (in seconds)
     */
    public Long getTokenRemainingTime(String token) {
        Date expiration = extractExpiration(token);
        long currentTime = System.currentTimeMillis();
        long expirationTime = expiration.getTime();
        
        if (expirationTime <= currentTime) {
            return 0L;
        }
        
        return (expirationTime - currentTime) / 1000; // Convert to seconds
    }
}