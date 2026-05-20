package com.alexdev.animerpgpomodoro.task.mapper;

import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
import com.alexdev.animerpgpomodoro.task.entity.Task;

public class TaskMapper {

    public static TaskResponse toResponse(Task task) {

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getPriority(),
                task.getXpReward(),
                task.getPomodoroSessions(),
                task.getDueDate(),
                task.getCategory().getName(),
                task.getCategory().getId(),
                task.getCreatedAt()
        );
    }
}