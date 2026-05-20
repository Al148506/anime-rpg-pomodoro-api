package com.alexdev.animerpgpomodoro.pomodoro.dto;

import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;

import java.time.LocalDateTime;

public record PomodoroResponse(
        String id,
        String name,
        PomodoroType type,
        Integer durationMinutes,
        Integer shortBreakMinutes,
        Integer longBreakMinutes,
        Integer sessionsBeforeLongBreak,
        String taskId,
        String taskTitle,
        RepetitionSettingsResponse repetitionSettings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
