package com.alexdev.animerpgpomodoro.pomodoro.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroRequest;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroResponse;
import com.alexdev.animerpgpomodoro.pomodoro.dto.RepetitionSettingsRequest;
import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import com.alexdev.animerpgpomodoro.pomodoro.entity.RepetitionSettings;
import com.alexdev.animerpgpomodoro.pomodoro.mapper.PomodoroMapper;
import com.alexdev.animerpgpomodoro.pomodoro.repository.PomodoroRepository;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PomodoroService {

    private final PomodoroRepository pomodoroRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;

    public PomodoroService(
            PomodoroRepository pomodoroRepository,
            TaskRepository taskRepository,
            CurrentUserService currentUserService
    ) {
        this.pomodoroRepository = pomodoroRepository;
        this.taskRepository = taskRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PomodoroResponse create(PomodoroRequest request) {
        User user = currentUserService.getAuthenticatedUser();
        LocalDateTime now = LocalDateTime.now();

        Pomodoro pomodoro = Pomodoro.builder()
                .name(request.name())
                .type(request.type())
                .durationMinutes(request.durationMinutes())
                .shortBreakMinutes(request.shortBreakMinutes())
                .longBreakMinutes(request.longBreakMinutes())
                .sessionsBeforeLongBreak(request.sessionsBeforeLongBreak())
                .task(resolveTask(request.taskId(), user))
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();

        applyRepetitionSettings(pomodoro, request.repetitionSettings(), now);

        return PomodoroMapper.toResponse(pomodoroRepository.save(pomodoro));
    }

    @Transactional(readOnly = true)
    public List<PomodoroResponse> findAll() {
        User user = currentUserService.getAuthenticatedUser();

        return pomodoroRepository.findByUserEmail(user.getEmail())
                .stream()
                .map(PomodoroMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PomodoroResponse findById(String id) {
        User user = currentUserService.getAuthenticatedUser();

        return PomodoroMapper.toResponse(findOwnedPomodoro(id, user));
    }

    @Transactional
    public PomodoroResponse update(String id, PomodoroRequest request) {
        User user = currentUserService.getAuthenticatedUser();
        Pomodoro pomodoro = findOwnedPomodoro(id, user);
        LocalDateTime now = LocalDateTime.now();

        pomodoro.setName(request.name());
        pomodoro.setType(request.type());
        pomodoro.setDurationMinutes(request.durationMinutes());
        pomodoro.setShortBreakMinutes(request.shortBreakMinutes());
        pomodoro.setLongBreakMinutes(request.longBreakMinutes());
        pomodoro.setSessionsBeforeLongBreak(request.sessionsBeforeLongBreak());
        pomodoro.setTask(resolveTask(request.taskId(), user));
        pomodoro.setUpdatedAt(now);
        applyRepetitionSettings(pomodoro, request.repetitionSettings(), now);

        return PomodoroMapper.toResponse(pomodoroRepository.save(pomodoro));
    }

    @Transactional
    public void delete(String id) {
        User user = currentUserService.getAuthenticatedUser();
        pomodoroRepository.delete(findOwnedPomodoro(id, user));
    }

    public Pomodoro findOwnedPomodoro(String id, User user) {
        return pomodoroRepository.findByIdAndUserEmail(id, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Pomodoro not found"));
    }

    private Task resolveTask(String taskId, User user) {
        if (taskId == null || taskId.isBlank()) {
            return null;
        }

        return taskRepository.findByIdAndUserEmail(taskId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private void applyRepetitionSettings(
            Pomodoro pomodoro,
            RepetitionSettingsRequest request,
            LocalDateTime now
    ) {
        if (request == null) {
            pomodoro.setRepetitionSettings(null);
            return;
        }

        RepetitionSettings settings = pomodoro.getRepetitionSettings();
        if (settings == null) {
            settings = RepetitionSettings.builder()
                    .pomodoro(pomodoro)
                    .createdAt(now)
                    .build();
            pomodoro.setRepetitionSettings(settings);
        }

        settings.setEnabled(request.enabled());
        settings.setRepeatCount(request.repeatCount());
        settings.setRepeatDaily(request.repeatDaily());
        settings.setUpdatedAt(now);
    }
}
