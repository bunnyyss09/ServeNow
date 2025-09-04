package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.request.BookingRequest;
import com.manvanth.servenow.dto.response.BookingResponse;
import com.manvanth.servenow.entity.Booking;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.exception.BookingException;
import com.manvanth.servenow.exception.ResourceNotFoundException;
import com.manvanth.servenow.repository.BookingRepository;
import com.manvanth.servenow.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;

    public BookingResponse createBooking(Long customerId, BookingRequest request) {
        log.info("Creating booking for customer ID: {}", customerId);

        User customer = userService.findUserEntityById(customerId);
        if (!customer.isCustomer()) {
            throw new BookingException("Only customers can create bookings");
        }

        com.manvanth.servenow.entity.Service service = serviceRepository.findById(request.getServiceId())
                .filter(com.manvanth.servenow.entity.Service::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", request.getServiceId()));

        if (!service.getIsAvailable()) {
            throw new BookingException("Service is currently not available for booking");
        }

        Booking booking = new Booking();
        booking.setService(service);
        booking.setCustomerId(customerId);
        booking.setProviderId(service.getProvider().getId());
        booking.setScheduledAt(request.getScheduledAt());
        booking.setNotes(request.getNotes());
        booking.setServiceAddress(request.getServiceAddress());
        booking.setTotalAmount(service.getBasePrice());
        booking.setStatus(Booking.BookingStatus.REQUESTED);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());

        return mapToResponse(savedBooking);
    }

    public Page<BookingResponse> getCustomerBookings(Long customerId, Pageable pageable) {
        return bookingRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    public Page<BookingResponse> getProviderBookings(Long providerId, Pageable pageable) {
        return bookingRepository.findByProvider_IdOrderByCreatedAtDesc(providerId, pageable)
                .map(this::mapToResponse);
    }

    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        
        // Check if user has access to this booking
        if (!booking.getCustomerId().equals(userId) && !booking.getProviderId().equals(userId)) {
            throw new BookingException("You don't have access to this booking");
        }
        
        return mapToResponse(booking);
    }

    public BookingResponse acceptBooking(Long bookingId, Long providerId) {
        log.info("Provider {} accepting booking {}", providerId, bookingId);
        
        Booking booking = findBookingByIdOrThrow(bookingId);
        validateProviderAccess(booking, providerId);
        
        if (booking.getStatus() != Booking.BookingStatus.REQUESTED) {
            throw new BookingException("Only pending bookings can be accepted");
        }
        
        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    public BookingResponse rejectBooking(Long bookingId, Long providerId) {
        log.info("Provider {} rejecting booking {}", providerId, bookingId);
        
        Booking booking = findBookingByIdOrThrow(bookingId);
        validateProviderAccess(booking, providerId);
        
        if (booking.getStatus() != Booking.BookingStatus.REQUESTED) {
            throw new BookingException("Only pending bookings can be rejected");
        }
        
        booking.setStatus(Booking.BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    public BookingResponse completeBooking(Long bookingId, Long providerId) {
        log.info("Provider {} completing booking {}", providerId, bookingId);
        
        Booking booking = findBookingByIdOrThrow(bookingId);
        validateProviderAccess(booking, providerId);
        
        if (booking.getStatus() != Booking.BookingStatus.ACCEPTED) {
            throw new BookingException("Only confirmed bookings can be completed");
        }
        
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    public BookingResponse cancelBooking(Long bookingId, Long customerId) {
        log.info("Customer {} cancelling booking {}", customerId, bookingId);
        
        Booking booking = findBookingByIdOrThrow(bookingId);
        
        if (!booking.getCustomerId().equals(customerId)) {
            throw new BookingException("You can only cancel your own bookings");
        }
        
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BookingException("Cannot cancel completed or already cancelled bookings");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    private Booking findBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
    }

    private void validateProviderAccess(Booking booking, Long providerId) {
        if (!booking.getProviderId().equals(providerId)) {
            throw new BookingException("You can only manage your own bookings");
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setServiceId(booking.getService().getId());
        response.setServiceTitle(booking.getService().getTitle());
        response.setCustomerId(booking.getCustomerId());
        response.setProviderId(booking.getProviderId());
        response.setScheduledAt(booking.getScheduledAt());
        response.setNotes(booking.getNotes());
        response.setServiceAddress(booking.getServiceAddress());
        response.setTotalAmount(booking.getTotalAmount());
        response.setTotalAmountDisplay("₹" + booking.getTotalAmount());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        // Get user names (simplified approach)
        try {
            User customer = userService.findUserEntityById(booking.getCustomerId());
            response.setCustomerName(customer.getFullName());
            
            User provider = userService.findUserEntityById(booking.getProviderId());
            response.setProviderName(provider.getFullName());
        } catch (Exception e) {
            log.warn("Could not fetch user names for booking {}", booking.getId());
        }

        return response;
    }
}