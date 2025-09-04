package com.manvanth.servenow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manvanth.servenow.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Access Denied Handler
 * Handles authorization errors when user lacks required permissions
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";

        log.warn("Access denied for user '{}' to: {} - {}", 
                username, request.getRequestURI(), accessDeniedException.getMessage());

        // Set response content type and status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Create standardized error response
        ApiResponse<Object> errorResponse = ApiResponse.error(
            "Access denied. You don't have permission to access this resource.",
            request.getRequestURI(),
            HttpServletResponse.SC_FORBIDDEN
        );

        // Write error response to output stream
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}