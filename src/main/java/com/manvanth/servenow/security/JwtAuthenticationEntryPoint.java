package com.manvanth.servenow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manvanth.servenow.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Authentication Entry Point
 * Handles authentication errors and returns standardized error responses
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        log.warn("Unauthorized access attempt to: {} - {}", request.getRequestURI(), authException.getMessage());

        // Set response content type and status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create standardized error response
        ApiResponse<Object> errorResponse = ApiResponse.error(
            "Authentication required. Please provide a valid access token.",
            request.getRequestURI(),
            HttpServletResponse.SC_UNAUTHORIZED
        );

        // Write error response to output stream
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}