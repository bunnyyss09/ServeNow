package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.request.ChangePasswordRequest;
import com.manvanth.servenow.dto.request.RegisterRequest;
import com.manvanth.servenow.dto.request.UpdateUserRequest;
import com.manvanth.servenow.dto.response.UserResponse;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.exception.ResourceNotFoundException;
import com.manvanth.servenow.exception.UserException;
import com.manvanth.servenow.exception.ValidationException;
import com.manvanth.servenow.mapper.UserMapper;
import com.manvanth.servenow.repository.RoleRepository;
import com.manvanth.servenow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user management operations
 * Implements business logic for user CRUD operations, authentication, and profile management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public UserResponse registerUser(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());

        // Validate password confirmation
        if (!registerRequest.isPasswordConfirmed()) {
            throw new ValidationException("Password and confirm password do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserException("Email address is already registered");
        }

        // Check if phone number already exists (if provided)
        if (registerRequest.getPhoneNumber() != null && 
            userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new UserException("Phone number is already registered");
        }

        // Convert DTO to entity
        User user = userMapper.toUser(registerRequest);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Assign role based on user type
        var roleOptional = roleRepository.findByNameAndIsActiveTrue(registerRequest.getUserType());
        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Role", "name", registerRequest.getUserType());
        }
        user.getRoles().add(roleOptional.get());

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return userMapper.toUserResponse(savedUser);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return userMapper.toUserResponse(user);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toUserResponse(user);
    }

    /**
     * Update user profile
     */
    public UserResponse updateUserProfile(Long userId, UpdateUserRequest updateRequest) {
        log.info("Updating profile for user ID: {}", userId);

        User user = findUserByIdOrThrow(userId);

        // Check if phone number is being changed and if it's already taken
        if (updateRequest.getPhoneNumber() != null && 
            !updateRequest.getPhoneNumber().equals(user.getPhoneNumber()) &&
            userRepository.existsByPhoneNumber(updateRequest.getPhoneNumber())) {
            throw new UserException("Phone number is already registered to another user");
        }

        // Update user fields
        userMapper.updateUserFromRequest(updateRequest, user);

        // If phone number is changed, mark as unverified
        if (updateRequest.getPhoneNumber() != null && 
            !updateRequest.getPhoneNumber().equals(user.getPhoneNumber())) {
            user.setIsPhoneVerified(false);
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return userMapper.toUserResponse(updatedUser);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing password for user ID: {}", userId);

        if (!changePasswordRequest.isNewPasswordConfirmed()) {
            throw new ValidationException("New password and confirm password do not match");
        }

        if (!changePasswordRequest.isDifferentFromCurrent()) {
            throw new ValidationException("New password must be different from current password");
        }

        User user = findUserByIdOrThrow(userId);

        // Verify current password
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Delete user (soft delete)
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        User user = findUserByIdOrThrow(userId);
        user.setIsActive(false);
        user.setEnabled(false);
        userRepository.save(user);

        log.info("User deleted successfully with ID: {}", userId);
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    /**
     * Search users by name or email
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchUsers(searchTerm, pageable)
                .map(userMapper::toUserResponse);
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    /**
     * Get users by role with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(String roleName, Pageable pageable) {
        return userRepository.findByRoleName(roleName, pageable)
                .map(userMapper::toUserResponse);
    }

    /**
     * Get all service providers
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllProviders() {
        return userRepository.findAllProviders().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    /**
     * Get all customers
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllCustomers() {
        return userRepository.findAllCustomers().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    /**
     * Get users within radius of location
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        return userRepository.findUsersWithinRadius(latitude, longitude, radiusKm).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    /**
     * Verify user email
     */
    public void verifyEmail(Long userId) {
        log.info("Verifying email for user ID: {}", userId);

        User user = findUserByIdOrThrow(userId);
        user.setIsEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified successfully for user ID: {}", userId);
    }

    /**
     * Verify user phone
     */
    public void verifyPhone(Long userId) {
        log.info("Verifying phone for user ID: {}", userId);

        User user = findUserByIdOrThrow(userId);
        user.setIsPhoneVerified(true);
        userRepository.save(user);

        log.info("Phone verified successfully for user ID: {}", userId);
    }

    /**
     * Enable/disable user account
     */
    public void toggleUserStatus(Long userId, boolean enabled) {
        log.info("Toggling user status for user ID: {} to: {}", userId, enabled);

        User user = findUserByIdOrThrow(userId);
        user.setEnabled(enabled);
        userRepository.save(user);

        log.info("User status toggled successfully for user ID: {}", userId);
    }

    /**
     * Check if email is available
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Check if phone number is available
     */
    @Transactional(readOnly = true)
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return !userRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countByRoleName("CUSTOMER");
        long totalProviders = userRepository.countByRoleName("PROVIDER");
        long totalAdmins = userRepository.countByRoleName("ADMIN");

        return new UserStats(totalUsers, totalCustomers, totalProviders, totalAdmins);
    }

    /**
     * Implementation of UserDetailsService for Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Helper method to find user by ID or throw exception
     */
    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .filter(User::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Find user entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public User findUserEntityById(Long userId) {
        return findUserByIdOrThrow(userId);
    }

    /**
     * Find user entity by email (for internal use)
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserEntityByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email);
    }

    /**
     * User statistics inner class
     */
    public record UserStats(long totalUsers, long totalCustomers, long totalProviders, long totalAdmins) {}
}