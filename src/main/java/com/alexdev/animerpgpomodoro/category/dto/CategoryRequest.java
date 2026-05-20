package com.alexdev.animerpgpomodoro.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 80, message = "Name must be at most 80 characters")
        String name,

        @NotBlank(message = "Slug is required")
        @Size(max = 80, message = "Slug must be at most 80 characters")
        String slug,

        @Size(max = 20, message = "Color must be at most 20 characters")
        String color,

        @Size(max = 20, message = "Emoji must be at most 20 characters")
        String emoji
) {
}