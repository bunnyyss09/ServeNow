package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.request.ReviewRequest;
import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.ReviewResponse;
import com.manvanth.servenow.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reviews", description = "Review and rating management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create review", description = "Create a review for a completed booking (Customer only)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.valueOf(userDetails.getUsername());
        ReviewResponse review = reviewService.createReview(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(review));
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Get service reviews", description = "Get paginated reviews for a specific service")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getServiceReviews(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getServiceReviews(serviceId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get provider reviews", description = "Get paginated reviews for a specific provider")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProviderReviews(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getProviderReviews(providerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get customer reviews", description = "Get reviews created by logged-in customer")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getCustomerReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.valueOf(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getCustomerReviews(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review details", description = "Get review details by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
}