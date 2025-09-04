package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity for handling transaction records
 * Integrates with payment gateways like Stripe for processing payments
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"booking"})
@ToString(exclude = {"booking"})
public class Payment extends BaseEntity {

    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid amount format")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    // Gateway information
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway")
    private PaymentGateway paymentGateway = PaymentGateway.STRIPE;

    @Column(name = "gateway_transaction_id", unique = true)
    private String gatewayTransactionId;

    @Column(name = "gateway_payment_intent_id")
    private String gatewayPaymentIntentId;

    @Column(name = "gateway_customer_id")
    private String gatewayCustomerId;

    // Payment processing details
    @Column(name = "processing_fee", precision = 10, scale = 2)
    private BigDecimal processingFee;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "platform_fee", precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "provider_amount", precision = 10, scale = 2)
    private BigDecimal providerAmount;

    // Payment card information (for display purposes only - never store full card details)
    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 20)
    private String cardBrand;

    @Column(name = "card_exp_month")
    private Integer cardExpMonth;

    @Column(name = "card_exp_year")
    private Integer cardExpYear;

    // Payment timing
    @Column(name = "authorized_at")
    private LocalDateTime authorizedAt;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // Refund information
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "gateway_refund_id")
    private String gatewayRefundId;

    // Failure information
    @Column(name = "failure_code", length = 50)
    private String failureCode;

    @Column(name = "failure_message", length = 500)
    private String failureMessage;

    // Additional metadata
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "receipt_email")
    private String receiptEmail;

    // Enums
    public enum PaymentStatus {
        PENDING("Pending"),
        AUTHORIZED("Authorized"),
        CAPTURED("Captured"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        CANCELLED("Cancelled"),
        REFUNDED("Refunded"),
        PARTIALLY_REFUNDED("Partially Refunded"),
        DISPUTED("Disputed");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isSuccess() {
            return this == COMPLETED || this == CAPTURED;
        }

        public boolean isFinal() {
            return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
        }
    }

    public enum PaymentMethod {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        BANK_TRANSFER("Bank Transfer"),
        DIGITAL_WALLET("Digital Wallet"),
        CASH("Cash"),
        CHECK("Check");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentGateway {
        STRIPE("Stripe"),
        PAYPAL("PayPal"),
        SQUARE("Square"),
        RAZORPAY("Razorpay"),
        INTERNAL("Internal");

        private final String displayName;

        PaymentGateway(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Payment(Booking booking, BigDecimal amount, PaymentMethod paymentMethod) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.currency = "INR";
        this.status = PaymentStatus.PENDING;
    }

    // Helper methods
    public boolean isSuccessful() {
        return status.isSuccess();
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.AUTHORIZED;
    }

    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED && 
               (refundAmount == null || refundAmount.compareTo(amount) < 0);
    }

    public BigDecimal getRefundableAmount() {
        if (!canBeRefunded()) return BigDecimal.ZERO;
        return amount.subtract(refundAmount == null ? BigDecimal.ZERO : refundAmount);
    }

    public void authorize() {
        this.status = PaymentStatus.AUTHORIZED;
        this.authorizedAt = LocalDateTime.now();
    }

    public void capture() {
        if (status != PaymentStatus.AUTHORIZED && status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot be captured in status: " + status);
        }
        this.status = PaymentStatus.CAPTURED;
        this.capturedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
        if (capturedAt == null) {
            capturedAt = LocalDateTime.now();
        }
    }

    public void fail(String failureCode, String failureMessage) {
        this.status = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
    }

    public void refund(BigDecimal refundAmount, String reason) {
        if (!canBeRefunded()) {
            throw new IllegalStateException("Payment cannot be refunded in current state");
        }

        BigDecimal totalRefunded = this.refundAmount == null ? BigDecimal.ZERO : this.refundAmount;
        totalRefunded = totalRefunded.add(refundAmount);

        if (totalRefunded.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }

        this.refundAmount = totalRefunded;
        this.refundReason = reason;
        this.refundedAt = LocalDateTime.now();

        if (totalRefunded.compareTo(amount) == 0) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }

    public String getDisplayCardInfo() {
        if (cardLastFour != null && cardBrand != null) {
            return String.format("%s ****%s", cardBrand, cardLastFour);
        }
        return paymentMethod.getDisplayName();
    }

    public String getAmountDisplay() {
        return String.format("₹%.2f %s", amount, currency);
    }

    @PrePersist
    protected void onPaymentCreate() {
        super.onCreate();
        
        // Calculate fees and amounts
        if (processingFee == null && paymentGateway == PaymentGateway.STRIPE) {
            // Stripe fee for India: 2.9% + ₹2.00
            processingFee = amount.multiply(new BigDecimal("0.029")).add(new BigDecimal("2.00"));
        }
        
        if (platformFee == null) {
            // Default platform fee: 5% of amount
            platformFee = amount.multiply(new BigDecimal("0.05"));
        }
        
        if (netAmount == null) {
            netAmount = amount.subtract(processingFee == null ? BigDecimal.ZERO : processingFee);
        }
        
        if (providerAmount == null) {
            providerAmount = netAmount.subtract(platformFee == null ? BigDecimal.ZERO : platformFee);
        }

        // Generate description if not provided
        if (description == null && booking != null) {
            description = String.format("Payment for booking #%d", booking.getId());
        }
    }
}