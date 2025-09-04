package com.manvanth.servenow.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    
    private Long id;
    private Long serviceId;
    private String serviceTitle;
    private Long customerId;
    private String customerName;
    private Long providerId;
    private String providerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}