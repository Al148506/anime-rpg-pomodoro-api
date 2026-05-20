package com.alexdev.animerpgpomodoro.pomodoro.dto;

import java.time.LocalDateTime;

public record RepetitionSettingsResponse(
        String id,
        boolean enabled,
        Integer repeatCount,
        boolean repeatDaily,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
