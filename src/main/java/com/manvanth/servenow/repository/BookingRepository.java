package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Page<Booking> findByCustomer_IdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    
    Page<Booking> findByProvider_IdOrderByCreatedAtDesc(Long providerId, Pageable pageable);
    
    List<Booking> findByProvider_IdAndStatusOrderByScheduledDateTimeAsc(Long providerId, Booking.BookingStatus status);
    
    List<Booking> findByCustomer_IdAndStatusOrderByScheduledDateTimeAsc(Long customerId, Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.scheduledDateTime BETWEEN :start AND :end AND b.provider.id = :providerId")
    List<Booking> findByProviderAndDateRange(@Param("providerId") Long providerId, 
                                           @Param("start") LocalDateTime start, 
                                           @Param("end") LocalDateTime end);
}