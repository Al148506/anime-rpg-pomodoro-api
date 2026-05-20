package com.alexdev.animerpgpomodoro.progression.dto;

import com.alexdev.animerpgpomodoro.progression.entity.AchievementType;

import java.time.LocalDateTime;

public record AchievementResponse(
        String id,
        String code,
        String title,
        String description,
        AchievementType type,
        Integer xpReward,
        LocalDateTime unlockedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
