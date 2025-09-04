package com.manvanth.servenow.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledAt;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Size(max = 200, message = "Service address cannot exceed 200 characters")
    private String serviceAddress;
}