package com.alexdev.animerpgpomodoro.pomodoro.mapper;

import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroResponse;
import com.alexdev.animerpgpomodoro.pomodoro.dto.RepetitionSettingsResponse;
import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import com.alexdev.animerpgpomodoro.pomodoro.entity.RepetitionSettings;

public class PomodoroMapper {

    private PomodoroMapper() {
    }

    public static PomodoroResponse toResponse(Pomodoro pomodoro) {
        return new PomodoroResponse(
                pomodoro.getId(),
                pomodoro.getName(),
                pomodoro.getType(),
                pomodoro.getDurationMinutes(),
                pomodoro.getShortBreakMinutes(),
                pomodoro.getLongBreakMinutes(),
                pomodoro.getSessionsBeforeLongBreak(),
                pomodoro.getTask() != null ? pomodoro.getTask().getId() : null,
                pomodoro.getTask() != null ? pomodoro.getTask().getTitle() : null,
                toResponse(pomodoro.getRepetitionSettings()),
                pomodoro.getCreatedAt(),
                pomodoro.getUpdatedAt()
        );
    }

    private static RepetitionSettingsResponse toResponse(RepetitionSettings settings) {
        if (settings == null) {
            return null;
        }

        return new RepetitionSettingsResponse(
                settings.getId(),
                settings.isEnabled(),
                settings.getRepeatCount(),
                settings.isRepeatDaily(),
                settings.getCreatedAt(),
                settings.getUpdatedAt()
        );
    }
}
