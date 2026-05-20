package com.alexdev.animerpgpomodoro.session.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.common.exception.BusinessRuleException;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.pomodoro.service.PomodoroService;
import com.alexdev.animerpgpomodoro.progression.service.PlayerProgressService;
import com.alexdev.animerpgpomodoro.session.dto.CompletePomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.dto.PomodoroSessionResponse;
import com.alexdev.animerpgpomodoro.session.dto.StartPomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.entity.PomodoroSession;
import com.alexdev.animerpgpomodoro.session.entity.PomodoroSessionStatus;
import com.alexdev.animerpgpomodoro.session.mapper.PomodoroSessionMapper;
import com.alexdev.animerpgpomodoro.session.repository.PomodoroSessionRepository;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PomodoroSessionService {

    private static final int FOCUS_SESSION_XP = 25;

    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final PomodoroService pomodoroService;
    private final TaskRepository taskRepository;
    private final PlayerProgressService playerProgressService;
    private final CurrentUserService currentUserService;

    public PomodoroSessionService(
            PomodoroSessionRepository pomodoroSessionRepository,
            PomodoroService pomodoroService,
            TaskRepository taskRepository,
            PlayerProgressService playerProgressService,
            CurrentUserService currentUserService
    ) {
        this.pomodoroSessionRepository = pomodoroSessionRepository;
        this.pomodoroService = pomodoroService;
        this.taskRepository = taskRepository;
        this.playerProgressService = playerProgressService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PomodoroSessionResponse start(StartPomodoroSessionRequest request) {
        User user = currentUserService.getAuthenticatedUser();
        LocalDateTime now = LocalDateTime.now();

        PomodoroSession session = PomodoroSession.builder()
                .user(user)
                .pomodoro(resolvePomodoro(request.pomodoroId(), user))
                .task(resolveTask(request.taskId(), user))
                .type(request.type())
                .status(PomodoroSessionStatus.IN_PROGRESS)
                .plannedDurationMinutes(request.plannedDurationMinutes())
                .startedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return PomodoroSessionMapper.toResponse(pomodoroSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<PomodoroSessionResponse> findAll() {
        User user = currentUserService.getAuthenticatedUser();

        return pomodoroSessionRepository.findByUserEmailOrderByStartedAtDesc(user.getEmail())
                .stream()
                .map(PomodoroSessionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PomodoroSessionResponse findById(String id) {
        User user = currentUserService.getAuthenticatedUser();
        return PomodoroSessionMapper.toResponse(findOwnedSession(id, user));
    }

    @Transactional
    public PomodoroSessionResponse complete(String id, CompletePomodoroSessionRequest request) {
        User user = currentUserService.getAuthenticatedUser();
        PomodoroSession session = findOwnedSession(id, user);

        assertInProgress(session);

        LocalDateTime now = LocalDateTime.now();
        session.setStatus(PomodoroSessionStatus.COMPLETED);
        session.setActualDurationMinutes(request.actualDurationMinutes());
        session.setCompletedAt(now);
        session.setNotes(request.notes());
        session.setUpdatedAt(now);

        if (session.getType() == PomodoroType.FOCUS) {
            playerProgressService.awardFocusSessionXp(user, FOCUS_SESSION_XP);
        }

        return PomodoroSessionMapper.toResponse(pomodoroSessionRepository.save(session));
    }

    @Transactional
    public PomodoroSessionResponse cancel(String id) {
        User user = currentUserService.getAuthenticatedUser();
        PomodoroSession session = findOwnedSession(id, user);

        assertInProgress(session);

        LocalDateTime now = LocalDateTime.now();
        session.setStatus(PomodoroSessionStatus.CANCELLED);
        session.setCompletedAt(now);
        session.setUpdatedAt(now);

        return PomodoroSessionMapper.toResponse(pomodoroSessionRepository.save(session));
    }

    private PomodoroSession findOwnedSession(String id, User user) {
        return pomodoroSessionRepository.findByIdAndUserEmail(id, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Pomodoro session not found"));
    }

    private Pomodoro resolvePomodoro(String pomodoroId, User user) {
        if (pomodoroId == null || pomodoroId.isBlank()) {
            return null;
        }

        return pomodoroService.findOwnedPomodoro(pomodoroId, user);
    }

    private Task resolveTask(String taskId, User user) {
        if (taskId == null || taskId.isBlank()) {
            return null;
        }

        return taskRepository.findByIdAndUserEmail(taskId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private void assertInProgress(PomodoroSession session) {
        if (session.getStatus() != PomodoroSessionStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Only in-progress sessions can change lifecycle state");
        }
    }
}
