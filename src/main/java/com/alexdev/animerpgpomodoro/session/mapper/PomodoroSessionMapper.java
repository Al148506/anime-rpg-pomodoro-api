package com.alexdev.animerpgpomodoro.session.mapper;

import com.alexdev.animerpgpomodoro.session.dto.PomodoroSessionResponse;
import com.alexdev.animerpgpomodoro.session.entity.PomodoroSession;

public class PomodoroSessionMapper {

    private PomodoroSessionMapper() {
    }

    public static PomodoroSessionResponse toResponse(PomodoroSession session) {
        return new PomodoroSessionResponse(
                session.getId(),
                session.getPomodoro() != null ? session.getPomodoro().getId() : null,
                session.getTask() != null ? session.getTask().getId() : null,
                session.getTask() != null ? session.getTask().getTitle() : null,
                session.getType(),
                session.getStatus(),
                session.getPlannedDurationMinutes(),
                session.getActualDurationMinutes(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getNotes(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
