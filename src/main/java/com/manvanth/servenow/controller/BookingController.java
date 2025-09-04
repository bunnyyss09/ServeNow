package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.request.BookingRequest;
import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.BookingResponse;
import com.manvanth.servenow.service.BookingService;
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
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookings", description = "Booking management endpoints")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create booking", description = "Create a new booking (Customer only)")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.createBooking(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(booking));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get customer bookings", description = "Get paginated bookings for logged-in customer")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getCustomerBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.valueOf(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> bookings = bookingService.getCustomerBookings(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get provider bookings", description = "Get paginated bookings for logged-in provider")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long providerId = Long.valueOf(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> bookings = bookingService.getProviderBookings(providerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get booking details", description = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/{bookingId}/accept")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Accept booking", description = "Accept a pending booking (Provider only)")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long providerId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.acceptBooking(bookingId, providerId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reject booking", description = "Reject a pending booking (Provider only)")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long providerId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.rejectBooking(bookingId, providerId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Complete booking", description = "Mark booking as completed (Provider only)")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long providerId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.completeBooking(bookingId, providerId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel booking", description = "Cancel a booking (Customer only)")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.valueOf(userDetails.getUsername());
        BookingResponse booking = bookingService.cancelBooking(bookingId, customerId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
}