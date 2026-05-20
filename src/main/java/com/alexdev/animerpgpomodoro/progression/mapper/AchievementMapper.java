package com.alexdev.animerpgpomodoro.progression.mapper;

import com.alexdev.animerpgpomodoro.progression.dto.AchievementResponse;
import com.alexdev.animerpgpomodoro.progression.entity.Achievement;

public class AchievementMapper {

    private AchievementMapper() {
    }

    public static AchievementResponse toResponse(Achievement achievement) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getCode(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getType(),
                achievement.getXpReward(),
                achievement.getUnlockedAt(),
                achievement.getCreatedAt(),
                achievement.getUpdatedAt()
        );
    }
}
