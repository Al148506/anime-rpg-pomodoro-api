package com.alexdev.animerpgpomodoro.session.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompletePomodoroSessionRequest(
        @NotNull(message = "Actual duration is required")
        @Min(1)
        @Max(240)
        Integer actualDurationMinutes,

        @Size(max = 1000)
        String notes
) {
}
