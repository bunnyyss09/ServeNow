package com.manvanth.servenow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Simple DTO for creating/updating services
 */
@Data
public class ServiceRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String priceUnit = "per service";
    private Integer estimatedDurationMinutes;
    private String serviceArea;
    private Boolean isAvailable = true;
}