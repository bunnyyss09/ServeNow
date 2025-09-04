package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.request.ServiceRequest;
import com.manvanth.servenow.dto.response.ServiceResponse;
import com.manvanth.servenow.entity.Category;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.exception.ResourceNotFoundException;
import com.manvanth.servenow.exception.UserException;
import com.manvanth.servenow.repository.CategoryRepository;
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

/**
 * Simple service for service listings operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceListingService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public Page<ServiceResponse> getAllServices(Pageable pageable) {
        return serviceRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    public Page<ServiceResponse> getServicesByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(categoryId, pageable)
                .map(this::mapToResponse);
    }

    public List<ServiceResponse> getServicesByProvider(Long providerId) {
        return serviceRepository.findByProviderIdAndIsActiveTrueOrderByCreatedAtDesc(providerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<ServiceResponse> searchServices(String searchTerm, Pageable pageable) {
        return serviceRepository.searchServices(searchTerm, pageable)
                .map(this::mapToResponse);
    }

    public List<ServiceResponse> getFeaturedServices() {
        return serviceRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse getServiceById(Long id) {
        com.manvanth.servenow.entity.Service service = findServiceByIdOrThrow(id);
        return mapToResponse(service);
    }

    public ServiceResponse getServiceBySlug(String slug) {
        com.manvanth.servenow.entity.Service service = serviceRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "slug", slug));
        return mapToResponse(service);
    }

    public ServiceResponse createService(Long providerId, ServiceRequest request) {
        log.info("Creating service for provider ID: {}", providerId);

        User provider = userService.findUserEntityById(providerId);
        if (!provider.isProvider()) {
            throw new UserException("User must be a provider to create services");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .filter(Category::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        com.manvanth.servenow.entity.Service service = new com.manvanth.servenow.entity.Service();
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setBasePrice(request.getBasePrice());
        service.setPriceUnit(request.getPriceUnit());
        service.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        service.setServiceArea(request.getServiceArea());
        service.setIsAvailable(request.getIsAvailable());
        service.setProvider(provider);
        service.setCategory(category);

        com.manvanth.servenow.entity.Service savedService = serviceRepository.save(service);
        log.info("Service created successfully with ID: {}", savedService.getId());

        return mapToResponse(savedService);
    }

    public ServiceResponse updateService(Long serviceId, Long providerId, ServiceRequest request) {
        log.info("Updating service ID: {} for provider ID: {}", serviceId, providerId);

        com.manvanth.servenow.entity.Service service = findServiceByIdOrThrow(serviceId);
        
        if (!service.getProvider().getId().equals(providerId)) {
            throw new UserException("You can only update your own services");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .filter(Category::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setBasePrice(request.getBasePrice());
        service.setPriceUnit(request.getPriceUnit());
        service.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        service.setServiceArea(request.getServiceArea());
        service.setIsAvailable(request.getIsAvailable());
        service.setCategory(category);

        com.manvanth.servenow.entity.Service updatedService = serviceRepository.save(service);
        log.info("Service updated successfully with ID: {}", updatedService.getId());

        return mapToResponse(updatedService);
    }

    public void deleteService(Long serviceId, Long providerId) {
        log.info("Deleting service ID: {} for provider ID: {}", serviceId, providerId);

        com.manvanth.servenow.entity.Service service = findServiceByIdOrThrow(serviceId);
        
        if (!service.getProvider().getId().equals(providerId)) {
            throw new UserException("You can only delete your own services");
        }

        service.setIsActive(false);
        serviceRepository.save(service);

        log.info("Service deleted successfully with ID: {}", serviceId);
    }

    private com.manvanth.servenow.entity.Service findServiceByIdOrThrow(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .filter(com.manvanth.servenow.entity.Service::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));
    }

    private ServiceResponse mapToResponse(com.manvanth.servenow.entity.Service service) {
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setTitle(service.getTitle());
        response.setDescription(service.getDescription());
        response.setBasePrice(service.getBasePrice());
        response.setPriceUnit(service.getPriceUnit());
        response.setPriceDisplay(service.getPriceDisplay());
        response.setEstimatedDurationMinutes(service.getEstimatedDurationMinutes());
        response.setServiceArea(service.getServiceArea());
        response.setIsAvailable(service.getIsAvailable());
        response.setAverageRating(service.getAverageRating());
        response.setTotalReviews(service.getTotalReviews());
        response.setCategoryName(service.getCategory().getName());
        response.setProviderName(service.getProvider().getFullName());
        response.setCreatedAt(service.getCreatedAt());
        return response;
    }
}