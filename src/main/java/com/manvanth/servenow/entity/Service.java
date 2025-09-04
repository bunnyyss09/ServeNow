package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Service entity representing services offered by providers
 * Contains all service details, pricing, and availability information
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"provider", "category", "bookings", "reviews"})
@ToString(exclude = {"provider", "category", "bookings", "reviews"})
public class Service extends BaseEntity {

    @NotBlank(message = "Service title is required")
    @Size(min = 5, max = 200, message = "Service title must be between 5 and 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Service description is required")
    @Size(min = 20, max = 2000, message = "Service description must be between 20 and 2000 characters")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    private PricingType pricingType = PricingType.FIXED;

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Size(max = 50, message = "Price unit must not exceed 50 characters")
    @Column(name = "price_unit", length = 50)
    private String priceUnit; // e.g., "per hour", "per visit", "per project"

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "service_area", length = 500)
    private String serviceArea; // Areas where service is provided

    @Column(name = "max_distance_km")
    private Double maxDistanceKm; // Maximum distance provider willing to travel

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "requires_quote")
    private Boolean requiresQuote = false;

    // Service images
    @ElementCollection
    @CollectionTable(
        name = "service_images",
        joinColumns = @JoinColumn(name = "service_id")
    )
    @Column(name = "image_url")
    private Set<String> imageUrls = new HashSet<>();

    // Service tags for better searchability
    @ElementCollection
    @CollectionTable(
        name = "service_tags",
        joinColumns = @JoinColumn(name = "service_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    // SEO and search
    @Column(name = "slug", unique = true, length = 200)
    private String slug;

    @Column(name = "meta_title", length = 100)
    private String metaTitle;

    @Column(name = "meta_description", length = 300)
    private String metaDescription;

    // Ratings and statistics
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "total_bookings")
    private Integer totalBookings = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    // Enums
    public enum PricingType {
        FIXED("Fixed Price"),
        HOURLY("Hourly Rate"),
        NEGOTIABLE("Negotiable"),
        QUOTE_BASED("Quote Based"),
        PACKAGE("Package Deal");

        private final String displayName;

        PricingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public void addImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            imageUrls.add(imageUrl);
        }
    }

    public void removeImage(String imageUrl) {
        imageUrls.remove(imageUrl);
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            tags.add(tag.trim().toLowerCase());
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void updateRating(BigDecimal newRating, Integer reviewCount) {
        this.averageRating = newRating;
        this.totalReviews = reviewCount;
    }

    public void incrementBookingCount() {
        this.totalBookings = (this.totalBookings == null ? 0 : this.totalBookings) + 1;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public String getPriceDisplay() {
        if (pricingType == PricingType.NEGOTIABLE) {
            return "Negotiable";
        } else if (pricingType == PricingType.QUOTE_BASED) {
            return "Quote Required";
        } else if (minPrice != null && maxPrice != null && !minPrice.equals(maxPrice)) {
            return String.format("₹%.2f - ₹%.2f%s", minPrice, maxPrice, 
                               priceUnit != null ? " " + priceUnit : "");
        } else {
            return String.format("₹%.2f%s", basePrice, 
                               priceUnit != null ? " " + priceUnit : "");
        }
    }

    private String generateSlug(String title) {
        if (title == null) return null;
        return title.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }

    @PrePersist
    @PreUpdate
    private void generateSlugFromTitle() {
        if (slug == null || slug.isEmpty()) {
            slug = generateSlug(title);
        }
        if (metaTitle == null || metaTitle.isEmpty()) {
            metaTitle = title.length() > 100 ? title.substring(0, 97) + "..." : title;
        }
        if (metaDescription == null || metaDescription.isEmpty() && description != null) {
            metaDescription = description.length() > 300 ? description.substring(0, 297) + "..." : description;
        }
    }
}