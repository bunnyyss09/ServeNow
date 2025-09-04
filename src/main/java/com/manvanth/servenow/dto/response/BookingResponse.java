package com.manvanth.servenow.dto.response;

import com.manvanth.servenow.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    
    private Long id;
    private Long serviceId;
    private String serviceTitle;
    private Long customerId;
    private String customerName;
    private Long providerId;
    private String providerName;
    private LocalDateTime scheduledAt;
    private String notes;
    private String serviceAddress;
    private BigDecimal totalAmount;
    private String totalAmountDisplay;
    private Booking.BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}