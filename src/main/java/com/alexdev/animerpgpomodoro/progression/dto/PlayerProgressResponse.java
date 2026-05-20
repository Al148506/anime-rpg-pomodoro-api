package com.alexdev.animerpgpomodoro.progression.dto;

import java.time.LocalDateTime;

public record PlayerProgressResponse(
        String id,
        Integer level,
        Integer totalXp,
        Integer completedFocusSessions,
        Integer currentStreakDays,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
