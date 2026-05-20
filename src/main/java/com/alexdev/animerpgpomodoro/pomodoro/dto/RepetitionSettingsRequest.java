package com.alexdev.animerpgpomodoro.pomodoro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RepetitionSettingsRequest(
        @NotNull(message = "Repetition enabled flag is required")
        Boolean enabled,

        @NotNull(message = "Repeat count is required")
        @Min(1)
        @Max(365)
        Integer repeatCount,

        @NotNull(message = "Repeat daily flag is required")
        Boolean repeatDaily
) {
}
