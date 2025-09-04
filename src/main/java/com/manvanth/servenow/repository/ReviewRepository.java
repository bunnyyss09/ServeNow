package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByService_IdOrderByCreatedAtDesc(Long serviceId, Pageable pageable);
    
    Page<Review> findByService_Provider_IdOrderByCreatedAtDesc(Long providerId, Pageable pageable);
    
    Page<Review> findByCustomer_IdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    
    Optional<Review> findByBooking_Id(Long bookingId);
    
    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.service.id = :serviceId")
    Double findAverageRatingByServiceId(@Param("serviceId") Long serviceId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.service.id = :serviceId")
    Long countByServiceId(@Param("serviceId") Long serviceId);
    
    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.service.provider.id = :providerId")
    Double findAverageRatingByProviderId(@Param("providerId") Long providerId);
}