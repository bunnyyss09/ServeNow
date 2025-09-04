package com.manvanth.servenow.repository;

import com.manvanth.servenow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Simple repository for Category operations
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByIsActiveTrueOrderBySortOrder();
    List<Category> findByParentCategoryIsNullAndIsActiveTrueOrderBySortOrder();
    List<Category> findByParentCategoryIdAndIsActiveTrueOrderBySortOrder(Long parentId);
    Optional<Category> findBySlugAndIsActiveTrue(String slug);
    boolean existsByNameAndIsActiveTrue(String name);
}