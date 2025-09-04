package com.manvanth.servenow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user profile responses
 * Contains user information for API responses (excludes sensitive data)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String profileImageUrl;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean enabled;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods for role checking
    public boolean isCustomer() {
        return roles != null && roles.contains("CUSTOMER");
    }

    public boolean isProvider() {
        return roles != null && roles.contains("PROVIDER");
    }

    public boolean isAdmin() {
        return roles != null && roles.contains("ADMIN");
    }

    public boolean isModerator() {
        return roles != null && roles.contains("MODERATOR");
    }
}