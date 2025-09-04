package com.manvanth.servenow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for password change requests
 * Contains current and new password for security validation
 */
@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
             message = "New password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;

    // Custom validation method
    public boolean isNewPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    public boolean isDifferentFromCurrent() {
        return currentPassword != null && !currentPassword.equals(newPassword);
    }
}