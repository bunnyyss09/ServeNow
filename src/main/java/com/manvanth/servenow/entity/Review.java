package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Review entity for customer feedback and ratings
 * Enables customers to rate and review service providers after booking completion
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"customer", "service", "booking"})
@ToString(exclude = {"customer", "service", "booking"})
public class Review extends BaseEntity {

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @NotNull(message = "Overall rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 1, message = "Rating must have at most 1 decimal place")
    @Column(name = "overall_rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal overallRating;

    // Detailed ratings
    @DecimalMin(value = "1.0", message = "Quality rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Quality rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 1, message = "Quality rating must have at most 1 decimal place")
    @Column(name = "quality_rating", precision = 2, scale = 1)
    private BigDecimal qualityRating;

    @DecimalMin(value = "1.0", message = "Communication rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Communication rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 1, message = "Communication rating must have at most 1 decimal place")
    @Column(name = "communication_rating", precision = 2, scale = 1)
    private BigDecimal communicationRating;

    @DecimalMin(value = "1.0", message = "Punctuality rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Punctuality rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 1, message = "Punctuality rating must have at most 1 decimal place")
    @Column(name = "punctuality_rating", precision = 2, scale = 1)
    private BigDecimal punctualityRating;

    @DecimalMin(value = "1.0", message = "Value for money rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Value for money rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 1, message = "Value for money rating must have at most 1 decimal place")
    @Column(name = "value_rating", precision = 2, scale = 1)
    private BigDecimal valueRating;

    @Size(min = 10, max = 1000, message = "Review comment must be between 10 and 1000 characters")
    @Column(name = "comment", length = 1000)
    private String comment;

    @Size(max = 500, message = "Title must not exceed 500 characters")
    @Column(name = "title", length = 500)
    private String title;

    // Review status and moderation
    @Column(name = "is_verified")
    private Boolean isVerified = true; // Verified if linked to completed booking

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status = ReviewStatus.PUBLISHED;

    @Size(max = 500, message = "Moderator notes must not exceed 500 characters")
    @Column(name = "moderator_notes", length = 500)
    private String moderatorNotes;

    // Helpful votes from other users
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Column(name = "not_helpful_count")
    private Integer notHelpfulCount = 0;

    // Provider response
    @Size(max = 1000, message = "Provider response must not exceed 1000 characters")
    @Column(name = "provider_response", length = 1000)
    private String providerResponse;

    @Column(name = "provider_response_date")
    private java.time.LocalDateTime providerResponseDate;

    // Enums
    public enum ReviewStatus {
        DRAFT("Draft"),
        PUBLISHED("Published"),
        HIDDEN("Hidden"),
        FLAGGED("Flagged"),
        REMOVED("Removed");

        private final String displayName;

        ReviewStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Review(User customer, Service service, Booking booking, BigDecimal overallRating, String comment) {
        this.customer = customer;
        this.service = service;
        this.booking = booking;
        this.overallRating = overallRating;
        this.comment = comment;
    }

    // Helper methods
    public boolean isPositive() {
        return overallRating.compareTo(new BigDecimal("3.5")) >= 0;
    }

    public boolean isNegative() {
        return overallRating.compareTo(new BigDecimal("2.5")) < 0;
    }

    public String getRatingDescription() {
        BigDecimal rating = overallRating;
        if (rating.compareTo(new BigDecimal("4.5")) >= 0) {
            return "Excellent";
        } else if (rating.compareTo(new BigDecimal("3.5")) >= 0) {
            return "Good";
        } else if (rating.compareTo(new BigDecimal("2.5")) >= 0) {
            return "Average";
        } else if (rating.compareTo(new BigDecimal("1.5")) >= 0) {
            return "Poor";
        } else {
            return "Terrible";
        }
    }

    public void incrementHelpfulCount() {
        this.helpfulCount = (this.helpfulCount == null ? 0 : this.helpfulCount) + 1;
    }

    public void incrementNotHelpfulCount() {
        this.notHelpfulCount = (this.notHelpfulCount == null ? 0 : this.notHelpfulCount) + 1;
    }

    public void addProviderResponse(String response) {
        this.providerResponse = response;
        this.providerResponseDate = java.time.LocalDateTime.now();
    }

    public boolean hasProviderResponse() {
        return providerResponse != null && !providerResponse.trim().isEmpty();
    }

    public Double getHelpfulRatio() {
        int total = (helpfulCount == null ? 0 : helpfulCount) + (notHelpfulCount == null ? 0 : notHelpfulCount);
        if (total == 0) return null;
        return (double) (helpfulCount == null ? 0 : helpfulCount) / total;
    }

    public BigDecimal getAverageDetailedRating() {
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;

        if (qualityRating != null) {
            sum = sum.add(qualityRating);
            count++;
        }
        if (communicationRating != null) {
            sum = sum.add(communicationRating);
            count++;
        }
        if (punctualityRating != null) {
            sum = sum.add(punctualityRating);
            count++;
        }
        if (valueRating != null) {
            sum = sum.add(valueRating);
            count++;
        }

        if (count == 0) return overallRating;
        return sum.divide(new BigDecimal(count), 1, java.math.RoundingMode.HALF_UP);
    }

    @PrePersist
    protected void onReviewCreate() {
        super.onCreate();
        
        // Auto-generate title if not provided
        if ((title == null || title.trim().isEmpty()) && comment != null) {
            String truncated = comment.length() > 50 ? comment.substring(0, 47) + "..." : comment;
            title = getRatingDescription() + " service - " + truncated;
        }

        // Set default detailed ratings to overall rating if not provided
        if (qualityRating == null) qualityRating = overallRating;
        if (communicationRating == null) communicationRating = overallRating;
        if (punctualityRating == null) punctualityRating = overallRating;
        if (valueRating == null) valueRating = overallRating;
    }

    // Convenience methods for compatibility with simplified review service
    public Long getServiceId() {
        return service != null ? service.getId() : null;
    }

    public void setServiceId(Long serviceId) {
        // This is a convenience method - actual service should be set via setService()
    }

    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public void setCustomerId(Long customerId) {
        // This is a convenience method - actual customer should be set via setCustomer()
    }

    public Long getProviderId() {
        return service != null ? service.getProvider().getId() : null;
    }

    public void setProviderId(Long providerId) {
        // This is a convenience method - derived from service.provider
    }

    public Integer getRating() {
        return overallRating != null ? overallRating.intValue() : null;
    }

    public void setRating(Integer rating) {
        this.overallRating = rating != null ? new BigDecimal(rating) : null;
    }
}