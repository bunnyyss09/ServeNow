package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.request.ChangePasswordRequest;
import com.manvanth.servenow.dto.request.UpdateUserRequest;
import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.UserResponse;
import com.manvanth.servenow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for user management operations
 * Handles user profile management, search, and administrative operations
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile and management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get authenticated user's profile information")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        log.info("Profile request for user: {}", email);
        
        UserResponse userResponse = userService.getUserByEmail(email);
        
        ApiResponse<UserResponse> response = ApiResponse.success(userResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update authenticated user's profile information")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        log.info("Profile update request for user: {}", email);
        
        // Get current user ID
        UserResponse currentUser = userService.getUserByEmail(email);
        
        UserResponse updatedUser = userService.updateUserProfile(currentUser.getId(), updateRequest);
        
        ApiResponse<UserResponse> response = ApiResponse.success(
            "Profile updated successfully", updatedUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Change password
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change authenticated user's password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        log.info("Password change request for user: {}", email);
        
        // Get current user ID
        UserResponse currentUser = userService.getUserByEmail(email);
        
        userService.changePassword(currentUser.getId(), changePasswordRequest);
        
        ApiResponse<Object> response = ApiResponse.success("Password changed successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID (Admin only)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get user by ID", description = "Get user information by ID (Admin/Moderator only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.info("Get user request for ID: {}", userId);
        
        UserResponse userResponse = userService.getUserById(userId);
        
        ApiResponse<UserResponse> response = ApiResponse.success(userResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all users with pagination (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get all users", description = "Get paginated list of all users (Admin/Moderator only)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Get all users request with pagination: {}", pageable);
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        
        ApiResponse<Page<UserResponse>> response = ApiResponse.success(users);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin/Moderator only)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Search users request with term: {}", searchTerm);
        
        Page<UserResponse> users = userService.searchUsers(searchTerm, pageable);
        
        ApiResponse<Page<UserResponse>> response = ApiResponse.success(users);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get users by role
     */
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get users by role", description = "Get users with specific role (Admin/Moderator only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable String roleName) {
        log.info("Get users by role request: {}", roleName);
        
        List<UserResponse> users = userService.getUsersByRole(roleName);
        
        ApiResponse<List<UserResponse>> response = ApiResponse.success(users);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all service providers
     */
    @GetMapping("/providers")
    @Operation(summary = "Get all providers", description = "Get all service providers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllProviders() {
        log.info("Get all providers request");
        
        List<UserResponse> providers = userService.getAllProviders();
        
        ApiResponse<List<UserResponse>> response = ApiResponse.success(providers);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all customers
     */
    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get all customers", description = "Get all customers (Admin/Moderator only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllCustomers() {
        log.info("Get all customers request");
        
        List<UserResponse> customers = userService.getAllCustomers();
        
        ApiResponse<List<UserResponse>> response = ApiResponse.success(customers);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get users within radius
     */
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby users", description = "Get users within specified radius of location")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersWithinRadius(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        
        log.info("Get nearby users request: lat={}, lng={}, radius={}", latitude, longitude, radiusKm);
        
        List<UserResponse> users = userService.getUsersWithinRadius(latitude, longitude, radiusKm);
        
        ApiResponse<List<UserResponse>> response = ApiResponse.success(users);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verify user email (Admin only)
     */
    @PutMapping("/{userId}/verify-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify user email", description = "Mark user email as verified (Admin only)")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@PathVariable Long userId) {
        log.info("Verify email request for user ID: {}", userId);
        
        userService.verifyEmail(userId);
        
        ApiResponse<Object> response = ApiResponse.success("Email verified successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verify user phone (Admin only)
     */
    @PutMapping("/{userId}/verify-phone")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify user phone", description = "Mark user phone as verified (Admin only)")
    public ResponseEntity<ApiResponse<Object>> verifyPhone(@PathVariable Long userId) {
        log.info("Verify phone request for user ID: {}", userId);
        
        userService.verifyPhone(userId);
        
        ApiResponse<Object> response = ApiResponse.success("Phone verified successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Enable/disable user account (Admin only)
     */
    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle user status", description = "Enable or disable user account (Admin only)")
    public ResponseEntity<ApiResponse<Object>> toggleUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {
        
        log.info("Toggle user status request for user ID: {} to: {}", userId, enabled);
        
        userService.toggleUserStatus(userId, enabled);
        
        ApiResponse<Object> response = ApiResponse.success(
            "User status updated successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete user account (Admin only)")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long userId) {
        log.info("Delete user request for ID: {}", userId);
        
        userService.deleteUser(userId);
        
        ApiResponse<Object> response = ApiResponse.success("User deleted successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if email address is available for registration")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmailAvailability(@RequestParam String email) {
        log.info("Check email availability for: {}", email);
        
        boolean available = userService.isEmailAvailable(email);
        
        Map<String, Boolean> result = Map.of("available", available);
        ApiResponse<Map<String, Boolean>> response = ApiResponse.success(result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check phone number availability
     */
    @GetMapping("/check-phone")
    @Operation(summary = "Check phone availability", description = "Check if phone number is available for registration")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPhoneAvailability(@RequestParam String phoneNumber) {
        log.info("Check phone availability for: {}", phoneNumber);
        
        boolean available = userService.isPhoneNumberAvailable(phoneNumber);
        
        Map<String, Boolean> result = Map.of("available", available);
        ApiResponse<Map<String, Boolean>> response = ApiResponse.success(result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user statistics (Admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get user statistics and counts (Admin only)")
    public ResponseEntity<ApiResponse<UserService.UserStats>> getUserStats() {
        log.info("Get user statistics request");
        
        UserService.UserStats stats = userService.getUserStats();
        
        ApiResponse<UserService.UserStats> response = ApiResponse.success(stats);
        
        return ResponseEntity.ok(response);
    }
}