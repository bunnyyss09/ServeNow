package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.request.ServiceRequest;
import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.ServiceResponse;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.service.ServiceListingService;
import com.manvanth.servenow.service.UserService;
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

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Services", description = "Service management endpoints")
public class ServiceController {

    private final ServiceListingService serviceListingService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all services", description = "Get paginated list of all active services")
    public ResponseEntity<ApiResponse<Page<ServiceResponse>>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceResponse> services = serviceListingService.getAllServices(pageable);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get services by category", description = "Get paginated services by category")
    public ResponseEntity<ApiResponse<Page<ServiceResponse>>> getServicesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceResponse> services = serviceListingService.getServicesByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/search")
    @Operation(summary = "Search services", description = "Search services by title and description")
    public ResponseEntity<ApiResponse<Page<ServiceResponse>>> searchServices(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceResponse> services = serviceListingService.searchServices(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured services", description = "Get list of featured services")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getFeaturedServices() {
        List<ServiceResponse> services = serviceListingService.getFeaturedServices();
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service by ID", description = "Get service details by ID")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable Long serviceId) {
        ServiceResponse service = serviceListingService.getServiceById(serviceId);
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get service by slug", description = "Get service details by slug")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceBySlug(@PathVariable String slug) {
        ServiceResponse service = serviceListingService.getServiceBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create service", description = "Create a new service (Provider only)")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(
            @Valid @RequestBody ServiceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Get provider ID from the authenticated user
        User provider = userService.findUserEntityByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long providerId = provider.getId();
        ServiceResponse service = serviceListingService.createService(providerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service));
    }

    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update service", description = "Update service details (Provider only)")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User provider = userService.findUserEntityByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long providerId = provider.getId();
        ServiceResponse service = serviceListingService.updateService(serviceId, providerId, request);
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete service", description = "Soft delete service (Provider only)")
    public ResponseEntity<ApiResponse<Object>> deleteService(
            @PathVariable Long serviceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User provider = userService.findUserEntityByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long providerId = provider.getId();
        serviceListingService.deleteService(serviceId, providerId);
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully"));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get services by provider", description = "Get all services by a specific provider")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getServicesByProvider(@PathVariable Long providerId) {
        List<ServiceResponse> services = serviceListingService.getServicesByProvider(providerId);
        return ResponseEntity.ok(ApiResponse.success(services));
    }
}