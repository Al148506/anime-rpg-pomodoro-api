package com.alexdev.animerpgpomodoro.session.dto;

import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartPomodoroSessionRequest(
        String pomodoroId,
        String taskId,

        @NotNull(message = "Session type is required")
        PomodoroType type,

        @NotNull(message = "Planned duration is required")
        @Min(1)
        @Max(240)
        Integer plannedDurationMinutes
) {
}
