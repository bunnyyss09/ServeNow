package com.manvanth.servenow.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Simple DTO for category responses
 */
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Long parentCategoryId;
    private String parentCategoryName;
    private Integer serviceCount;
    private LocalDateTime createdAt;
}