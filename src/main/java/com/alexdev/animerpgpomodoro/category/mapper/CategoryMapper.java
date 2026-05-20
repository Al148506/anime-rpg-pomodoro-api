package com.alexdev.animerpgpomodoro.category.mapper;

import com.alexdev.animerpgpomodoro.category.dto.CategoryResponse;
import com.alexdev.animerpgpomodoro.category.entity.Category;

public class CategoryMapper {

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getColor(),
                category.getEmoji()
        );
    }
}