package com.manvanth.servenow.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simple DTO for service responses
 */
@Data
public class ServiceResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal basePrice;
    private String priceUnit;
    private String priceDisplay;
    private Integer estimatedDurationMinutes;
    private String serviceArea;
    private Boolean isAvailable;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private String categoryName;
    private String providerName;
    private LocalDateTime createdAt;
}