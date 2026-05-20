package com.alexdev.animerpgpomodoro.task.dto;

import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record TaskRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 120)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Priority is required")
        TaskPriority priority,

        @NotNull(message = "XP reward is required")
        @Min(1)
        Integer xpReward,

        @NotNull(message = "Pomodoro sessions are required")
        @Min(1)
        Integer pomodoroSessions,

        LocalDateTime dueDate,

        @NotBlank(message = "Category ID is required")
        String categoryId
) {
}