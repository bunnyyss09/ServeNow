package com.manvanth.servenow.service;

import com.manvanth.servenow.dto.response.CategoryResponse;
import com.manvanth.servenow.entity.Category;
import com.manvanth.servenow.exception.ResourceNotFoundException;
import com.manvanth.servenow.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple service for category operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrder()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getTopLevelCategories() {
        return categoryRepository.findByParentCategoryIsNullAndIsActiveTrueOrderBySortOrder()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getSubCategories(Long parentId) {
        return categoryRepository.findByParentCategoryIdAndIsActiveTrueOrderBySortOrder(parentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .filter(Category::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToResponse(category);
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return mapToResponse(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setSlug(category.getSlug());
        response.setSortOrder(category.getSortOrder());
        response.setIsFeatured(category.getIsFeatured());
        response.setCreatedAt(category.getCreatedAt());
        
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
            response.setParentCategoryName(category.getParentCategory().getName());
        }
        
        response.setServiceCount(category.getServices().size());
        return response;
    }
}