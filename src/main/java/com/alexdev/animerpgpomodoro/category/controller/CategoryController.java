package com.alexdev.animerpgpomodoro.category.controller;

import com.alexdev.animerpgpomodoro.category.dto.CategoryRequest;
import com.alexdev.animerpgpomodoro.category.dto.CategoryResponse;
import com.alexdev.animerpgpomodoro.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        return categoryService.createCategory(request);
    }
}