package com.alexdev.animerpgpomodoro.progression.mapper;

import com.alexdev.animerpgpomodoro.progression.dto.PlayerProgressResponse;
import com.alexdev.animerpgpomodoro.progression.entity.PlayerProgress;

public class PlayerProgressMapper {

    private PlayerProgressMapper() {
    }

    public static PlayerProgressResponse toResponse(PlayerProgress progress) {
        return new PlayerProgressResponse(
                progress.getId(),
                progress.getLevel(),
                progress.getTotalXp(),
                progress.getCompletedFocusSessions(),
                progress.getCurrentStreakDays(),
                progress.getCreatedAt(),
                progress.getUpdatedAt()
        );
    }
}
