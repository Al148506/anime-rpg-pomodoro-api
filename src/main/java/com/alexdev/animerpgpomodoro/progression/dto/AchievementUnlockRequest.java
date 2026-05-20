package com.alexdev.animerpgpomodoro.progression.dto;

import com.alexdev.animerpgpomodoro.progression.entity.AchievementType;
import jakarta.validation.constraints.*;

public record AchievementUnlockRequest(
        @NotBlank(message = "Achievement code is required")
        @Size(max = 80)
        String code,

        @NotBlank(message = "Achievement title is required")
        @Size(max = 120)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Achievement type is required")
        AchievementType type,

        @NotNull(message = "XP reward is required")
        @Min(0)
        @Max(10000)
        Integer xpReward
) {
}
