package com.manvanth.servenow.controller;

import com.manvanth.servenow.dto.response.ApiResponse;
import com.manvanth.servenow.dto.response.CategoryResponse;
import com.manvanth.servenow.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Simple controller for category operations
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all active categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/top-level")
    @Operation(summary = "Get top-level categories", description = "Get categories without parent")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getTopLevelCategories() {
        List<CategoryResponse> categories = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{categoryId}/subcategories")
    @Operation(summary = "Get subcategories", description = "Get subcategories of a parent category")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubCategories(@PathVariable Long categoryId) {
        List<CategoryResponse> subcategories = categoryService.getSubCategories(categoryId);
        return ResponseEntity.ok(ApiResponse.success(subcategories));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Get category details by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Get category details by slug")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
}