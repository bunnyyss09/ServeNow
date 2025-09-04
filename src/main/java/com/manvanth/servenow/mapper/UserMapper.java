package com.manvanth.servenow.mapper;

import com.manvanth.servenow.dto.request.RegisterRequest;
import com.manvanth.servenow.dto.request.UpdateUserRequest;
import com.manvanth.servenow.dto.response.UserResponse;
import com.manvanth.servenow.entity.Role;
import com.manvanth.servenow.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User entity and DTOs
 * Handles conversion between User entities and various DTOs
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "roles", expression = "java(mapRolesToStrings(user.getRoles()))")
    UserResponse toUserResponse(User user);

    /**
     * Convert RegisterRequest DTO to User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "roles", ignore = true) // Roles will be assigned separately
    @Mapping(target = "providedServices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "isEmailVerified", constant = "false")
    @Mapping(target = "isPhoneVerified", constant = "false")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toUser(RegisterRequest registerRequest);

    /**
     * Update User entity from UpdateUserRequest DTO
     * Only updates non-null fields from the request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true) // Email cannot be updated
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "providedServices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isPhoneVerified", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateUserFromRequest(UpdateUserRequest updateRequest, @MappingTarget User user);

    /**
     * Helper method to convert roles to string set
     */
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                   .map(Role::getName)
                   .collect(Collectors.toSet());
    }

    /**
     * Partial update method that only updates provided fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "providedServices", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isPhoneVerified", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void partialUpdate(UpdateUserRequest updateRequest, @MappingTarget User user);
}