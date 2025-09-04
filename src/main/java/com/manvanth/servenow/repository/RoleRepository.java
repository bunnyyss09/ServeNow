package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity operations
 * Provides CRUD operations and custom query methods for role management
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * Used for role-based operations and user role assignment
     */
    Optional<Role> findByName(String name);

    /**
     * Find role by name and active status
     */
    Optional<Role> findByNameAndIsActiveTrue(String name);

    /**
     * Check if role name already exists
     * Used for role creation validation
     */
    boolean existsByName(String name);

    /**
     * Find all active roles
     */
    List<Role> findByIsActiveTrueOrderByName();

    /**
     * Find roles by user ID
     * Used to get all roles assigned to a specific user
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId AND r.isActive = true")
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * Count users for each role
     * Used for analytics and reporting
     */
    @Query("SELECT r.name, COUNT(u) FROM Role r LEFT JOIN r.users u " +
           "WHERE r.isActive = true AND (u.isActive = true OR u.isActive IS NULL) " +
           "GROUP BY r.name")
    List<Object[]> countUsersByRole();

    /**
     * Find default customer role
     */
    @Query("SELECT r FROM Role r WHERE r.name = 'CUSTOMER' AND r.isActive = true")
    Optional<Role> findCustomerRole();

    /**
     * Find default provider role
     */
    @Query("SELECT r FROM Role r WHERE r.name = 'PROVIDER' AND r.isActive = true")
    Optional<Role> findProviderRole();

    /**
     * Find admin roles
     */
    @Query("SELECT r FROM Role r WHERE r.name IN ('ADMIN', 'MODERATOR') AND r.isActive = true")
    List<Role> findAdminRoles();
}