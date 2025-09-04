package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Simple repository for Service operations
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    Page<Service> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Service> findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId, Pageable pageable);
    List<Service> findByProviderIdAndIsActiveTrueOrderByCreatedAtDesc(Long providerId);
    Optional<Service> findBySlugAndIsActiveTrue(String slug);
    
    @Query("SELECT s FROM Service s WHERE s.isActive = true AND " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Service> searchServices(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    List<Service> findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();
}