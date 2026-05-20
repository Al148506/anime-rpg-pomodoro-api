package com.alexdev.animerpgpomodoro.category.service;

import com.alexdev.animerpgpomodoro.category.dto.CategoryRequest;
import com.alexdev.animerpgpomodoro.category.dto.CategoryResponse;
import com.alexdev.animerpgpomodoro.category.entity.Category;
import com.alexdev.animerpgpomodoro.category.mapper.CategoryMapper;
import com.alexdev.animerpgpomodoro.category.repository.CategoryRepository;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException(
                    "Category slug already exists"
            );
        }
        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .color(request.color())
                .emoji(request.emoji())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponse(savedCategory);
    }
}