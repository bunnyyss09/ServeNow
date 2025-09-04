package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Category entity for organizing services into different types
 * Examples: Home Services, Education, Health & Wellness, etc.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"services", "parentCategory", "subCategories"})
@ToString(exclude = {"services", "parentCategory", "subCategories"})
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "slug", unique = true, length = 100)
    private String slug;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    // Self-referential relationship for hierarchical categories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Category> subCategories = new HashSet<>();

    // Services in this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Service> services = new HashSet<>();

    // Convenience constructor
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.slug = generateSlug(name);
    }

    // Helper methods
    public boolean isTopLevel() {
        return parentCategory == null;
    }

    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }

    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Category subCategory) {
        subCategories.remove(subCategory);
        subCategory.setParentCategory(null);
    }

    public void addService(Service service) {
        services.add(service);
        service.setCategory(this);
    }

    public void removeService(Service service) {
        services.remove(service);
        service.setCategory(null);
    }

    private String generateSlug(String name) {
        if (name == null) return null;
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }

    @PrePersist
    @PreUpdate
    private void generateSlugFromName() {
        if (slug == null || slug.isEmpty()) {
            slug = generateSlug(name);
        }
    }
}