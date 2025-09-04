package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * Provides CRUD operations and custom query methods for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * Used for authentication and user lookup
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email address and active status
     * Used for authentication with account status check
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Check if email already exists
     * Used for registration validation
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone number already exists
     * Used for registration validation
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find users by role name
     * Used for role-based user queries
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Find users by role name with pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Find all service providers
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'PROVIDER' AND u.isActive = true")
    List<User> findAllProviders();

    /**
     * Find all customers
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'CUSTOMER' AND u.isActive = true")
    List<User> findAllCustomers();

    /**
     * Find users by location proximity
     * Used for location-based service matching
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(:latitude IS NULL OR :longitude IS NULL OR " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * " +
           "cos(radians(u.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(u.latitude)))) <= :radiusKm)")
    List<User> findUsersWithinRadius(@Param("latitude") Double latitude, 
                                   @Param("longitude") Double longitude, 
                                   @Param("radiusKm") Double radiusKm);

    /**
     * Find users by city
     */
    List<User> findByCityIgnoreCaseAndIsActiveTrue(String city);

    /**
     * Find users by state
     */
    List<User> findByStateIgnoreCaseAndIsActiveTrue(String state);

    /**
     * Search users by name or email
     * Used for admin user search functionality
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find users with email verification status
     */
    List<User> findByIsEmailVerifiedAndIsActiveTrue(Boolean isEmailVerified);

    /**
     * Find users with phone verification status
     */
    List<User> findByIsPhoneVerifiedAndIsActiveTrue(Boolean isPhoneVerified);

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    Long countByRoleName(@Param("roleName") String roleName);

    /**
     * Find recently registered users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(Pageable pageable);

    /**
     * Find users by account status
     */
    List<User> findByEnabledAndIsActiveTrue(Boolean enabled);

    /**
     * Custom query to find providers with services in a specific category
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r JOIN u.providedServices s " +
           "WHERE r.name = 'PROVIDER' AND u.isActive = true AND s.isActive = true " +
           "AND s.category.id = :categoryId")
    List<User> findProvidersByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Find providers with average rating above threshold
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.providedServices s " +
           "WHERE u.isActive = true AND s.isActive = true " +
           "AND s.averageRating >= :minRating")
    List<User> findProvidersWithMinRating(@Param("minRating") Double minRating);
}