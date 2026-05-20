package com.alexdev.animerpgpomodoro.category.dto;

public record CategoryResponse(
        String id,
        String name,
        String slug,
        String color,
        String emoji
) {
}