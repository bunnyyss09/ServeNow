package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking entity representing service requests and their lifecycle
 * Manages the complete booking process from request to completion
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"customer", "service", "payment", "review"})
@ToString(exclude = {"customer", "service", "payment", "review"})
public class Booking extends BaseEntity {

    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Provider is derived from service.provider, but we store it for query performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @NotNull(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.REQUESTED;

    @NotNull(message = "Scheduled date and time is required")
    @Future(message = "Scheduled date and time must be in the future")
    @Column(name = "scheduled_date_time", nullable = false)
    private LocalDateTime scheduledDateTime;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // Pricing information
    @Column(name = "quoted_price", precision = 10, scale = 2)
    private BigDecimal quotedPrice;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "currency", length = 3)
    private String currency = "INR";

    // Location information
    @Size(max = 500, message = "Service address must not exceed 500 characters")
    @Column(name = "service_address", length = 500)
    private String serviceAddress;

    @Column(name = "service_latitude")
    private Double serviceLatitude;

    @Column(name = "service_longitude")
    private Double serviceLongitude;

    // Communication
    @Size(max = 1000, message = "Customer notes must not exceed 1000 characters")
    @Column(name = "customer_notes", length = 1000)
    private String customerNotes;

    @Size(max = 1000, message = "Provider notes must not exceed 1000 characters")
    @Column(name = "provider_notes", length = 1000)
    private String providerNotes;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    // Status tracking
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by")
    private CancelledBy cancelledBy;

    // Relationships
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review;

    // Enums
    public enum BookingStatus {
        REQUESTED("Requested"),
        ACCEPTED("Accepted"),
        REJECTED("Rejected"),
        CONFIRMED("Confirmed"), // After payment
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        DISPUTE("Dispute");

        private final String displayName;

        BookingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this == ACCEPTED || this == CONFIRMED || this == IN_PROGRESS;
        }

        public boolean isFinal() {
            return this == COMPLETED || this == CANCELLED || this == REJECTED;
        }
    }

    public enum CancelledBy {
        CUSTOMER("Customer"),
        PROVIDER("Provider"),
        ADMIN("Admin"),
        SYSTEM("System");

        private final String displayName;

        CancelledBy(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public boolean canBeAccepted() {
        return status == BookingStatus.REQUESTED;
    }

    public boolean canBeRejected() {
        return status == BookingStatus.REQUESTED;
    }

    public boolean canBeStarted() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.ACCEPTED;
    }

    public boolean canBeCompleted() {
        return status == BookingStatus.IN_PROGRESS;
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.REQUESTED || 
               status == BookingStatus.ACCEPTED || 
               status == BookingStatus.CONFIRMED;
    }

    public void accept() {
        if (!canBeAccepted()) {
            throw new IllegalStateException("Booking cannot be accepted in current status: " + status);
        }
        this.status = BookingStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (!canBeRejected()) {
            throw new IllegalStateException("Booking cannot be rejected in current status: " + status);
        }
        this.status = BookingStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = CancelledBy.PROVIDER;
    }

    public void start() {
        if (!canBeStarted()) {
            throw new IllegalStateException("Booking cannot be started in current status: " + status);
        }
        this.status = BookingStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.actualStartTime = LocalDateTime.now();
    }

    public void complete() {
        if (!canBeCompleted()) {
            throw new IllegalStateException("Booking cannot be completed in current status: " + status);
        }
        this.status = BookingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.actualEndTime = LocalDateTime.now();
    }

    public void cancel(CancelledBy cancelledBy, String reason) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled in current status: " + status);
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
    }

    public Integer getActualDurationMinutes() {
        if (actualStartTime != null && actualEndTime != null) {
            return (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return null;
    }

    public String getStatusDisplayName() {
        return status.getDisplayName();
    }

    @PrePersist
    protected void onBookingCreate() {
        super.onCreate();
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (provider == null && service != null) {
            provider = service.getProvider();
        }
        if (quotedPrice == null && service != null) {
            quotedPrice = service.getBasePrice();
        }
        if (estimatedDurationMinutes == null && service != null) {
            estimatedDurationMinutes = service.getEstimatedDurationMinutes();
        }
    }
}