package com.alexdev.animerpgpomodoro.session.dto;

import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.session.entity.PomodoroSessionStatus;

import java.time.LocalDateTime;

public record PomodoroSessionResponse(
        String id,
        String pomodoroId,
        String taskId,
        String taskTitle,
        PomodoroType type,
        PomodoroSessionStatus status,
        Integer plannedDurationMinutes,
        Integer actualDurationMinutes,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
