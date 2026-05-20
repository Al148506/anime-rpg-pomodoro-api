package com.alexdev.animerpgpomodoro.pomodoro.dto;

import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record PomodoroRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120)
        String name,

        @NotNull(message = "Pomodoro type is required")
        PomodoroType type,

        @NotNull(message = "Duration is required")
        @Min(1)
        @Max(180)
        Integer durationMinutes,

        @NotNull(message = "Short break duration is required")
        @Min(1)
        @Max(60)
        Integer shortBreakMinutes,

        @NotNull(message = "Long break duration is required")
        @Min(1)
        @Max(60)
        Integer longBreakMinutes,

        @NotNull(message = "Sessions before long break is required")
        @Min(1)
        @Max(12)
        Integer sessionsBeforeLongBreak,

        String taskId,

        @Valid
        RepetitionSettingsRequest repetitionSettings
) {
}
