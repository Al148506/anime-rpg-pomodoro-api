package com.alexdev.animerpgpomodoro.task.dto;

import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;

import java.time.LocalDateTime;

public record TaskResponse(
        String id,
        String title,
        String description,
        boolean completed,
        TaskPriority priority,
        Integer xpReward,
        Integer pomodoroSessions,
        LocalDateTime dueDate,
        String categoryName,
        String categoryId,
        LocalDateTime createdAt
) {
}