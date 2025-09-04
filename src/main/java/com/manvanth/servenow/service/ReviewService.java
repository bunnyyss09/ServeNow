package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.request.ReviewRequest;
import com.manvanth.servenow.dto.response.ReviewResponse;
import com.manvanth.servenow.entity.Booking;
import com.manvanth.servenow.entity.Review;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.exception.ResourceNotFoundException;
import com.manvanth.servenow.exception.ValidationException;
import com.manvanth.servenow.repository.BookingRepository;
import com.manvanth.servenow.repository.ReviewRepository;
import com.manvanth.servenow.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;

    public ReviewResponse createReview(Long customerId, ReviewRequest request) {
        log.info("Creating review for customer ID: {}", customerId);

        // Find the booking
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        // Validate customer owns this booking
        if (!booking.getCustomerId().equals(customerId)) {
            throw new ValidationException("You can only review your own bookings");
        }

        // Validate booking is completed
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new ValidationException("You can only review completed bookings");
        }

        // Check if review already exists
        if (reviewRepository.findByBooking_Id(request.getBookingId()).isPresent()) {
            throw new ValidationException("You have already reviewed this booking");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setServiceId(booking.getService().getId());
        review.setCustomerId(customerId);
        review.setProviderId(booking.getProviderId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        
        // Update service ratings
        updateServiceRatings(booking.getService().getId());

        log.info("Review created successfully with ID: {}", savedReview.getId());
        return mapToResponse(savedReview);
    }

    public Page<ReviewResponse> getServiceReviews(Long serviceId, Pageable pageable) {
        return reviewRepository.findByService_IdOrderByCreatedAtDesc(serviceId, pageable)
                .map(this::mapToResponse);
    }

    public Page<ReviewResponse> getProviderReviews(Long providerId, Pageable pageable) {
        return reviewRepository.findByService_Provider_IdOrderByCreatedAtDesc(providerId, pageable)
                .map(this::mapToResponse);
    }

    public Page<ReviewResponse> getCustomerReviews(Long customerId, Pageable pageable) {
        return reviewRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        return mapToResponse(review);
    }

    private void updateServiceRatings(Long serviceId) {
        try {
            Double averageRating = reviewRepository.findAverageRatingByServiceId(serviceId);
            Long totalReviews = reviewRepository.countByServiceId(serviceId);

            com.manvanth.servenow.entity.Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));

            service.setAverageRating(averageRating != null ? BigDecimal.valueOf(averageRating) : BigDecimal.ZERO);
            service.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);

            serviceRepository.save(service);
            log.info("Updated service {} ratings: {} stars, {} reviews", serviceId, averageRating, totalReviews);
        } catch (Exception e) {
            log.error("Failed to update service ratings for service {}", serviceId, e);
        }
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setServiceId(review.getServiceId());
        response.setCustomerId(review.getCustomerId());
        response.setProviderId(review.getProviderId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        // Get additional details
        try {
            response.setServiceTitle(review.getBooking().getService().getTitle());
            
            User customer = userService.findUserEntityById(review.getCustomerId());
            response.setCustomerName(customer.getFullName());
            
            User provider = userService.findUserEntityById(review.getProviderId());
            response.setProviderName(provider.getFullName());
        } catch (Exception e) {
            log.warn("Could not fetch additional details for review {}", review.getId());
        }

        return response;
    }
}