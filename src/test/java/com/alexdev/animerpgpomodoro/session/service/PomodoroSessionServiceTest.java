package com.alexdev.animerpgpomodoro.session.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
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
import com.alexdev.animerpgpomodoro.session.repository.PomodoroSessionRepository;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import com.alexdev.animerpgpomodoro.task.repository.TaskRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PomodoroSessionServiceTest {

    @Mock
    private PomodoroSessionRepository pomodoroSessionRepository;

    @Mock
    private PomodoroService pomodoroService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PlayerProgressService playerProgressService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PomodoroSessionService pomodoroSessionService;

    @Captor
    private ArgumentCaptor<PomodoroSession> sessionCaptor;

    private static final String SESSION_ID = "session-uuid";
    private static final String POMODORO_ID = "pomodoro-uuid";
    private static final String TASK_ID = "task-uuid";
    private static final String USER_ID = "user-uuid";
    private static final String USER_EMAIL = "user@example.com";

    private User createUser() {
        return User.builder()
                .id(USER_ID)
                .username("testUser")
                .email(USER_EMAIL)
                .password("encoded")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Task createTask(User user) {
        return Task.builder()
                .id(TASK_ID)
                .title("Test Task")
                .completed(false)
                .priority(TaskPriority.MEDIUM)
                .xpReward(25)
                .pomodoroSessions(2)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Pomodoro createPomodoro(User user) {
        return Pomodoro.builder()
                .id(POMODORO_ID)
                .name("Focus Session")
                .type(PomodoroType.FOCUS)
                .durationMinutes(25)
                .shortBreakMinutes(5)
                .longBreakMinutes(15)
                .sessionsBeforeLongBreak(4)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private PomodoroSession createSession(User user, PomodoroSessionStatus status) {
        return PomodoroSession.builder()
                .id(SESSION_ID)
                .user(user)
                .type(PomodoroType.FOCUS)
                .status(status)
                .plannedDurationMinutes(25)
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class Start {

        @Test
        void shouldCreateAndReturnSessionResponseWhenDataIsValid() {
            var user = createUser();
            var pomodoro = createPomodoro(user);
            var task = createTask(user);
            var request = new StartPomodoroSessionRequest(
                    POMODORO_ID, TASK_ID, PomodoroType.FOCUS, 25
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroService.findOwnedPomodoro(POMODORO_ID, user)).thenReturn(pomodoro);
            when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
            when(pomodoroSessionRepository.save(any(PomodoroSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroSessionResponse result = pomodoroSessionService.start(request);

            assertThat(result.type()).isEqualTo(PomodoroType.FOCUS);
            assertThat(result.status()).isEqualTo(PomodoroSessionStatus.IN_PROGRESS);
            assertThat(result.plannedDurationMinutes()).isEqualTo(25);
            assertThat(result.pomodoroId()).isEqualTo(POMODORO_ID);
            assertThat(result.taskId()).isEqualTo(TASK_ID);
            assertThat(result.taskTitle()).isEqualTo("Test Task");
            verify(pomodoroSessionRepository).save(sessionCaptor.capture());
            assertThat(sessionCaptor.getValue().getStatus()).isEqualTo(PomodoroSessionStatus.IN_PROGRESS);
        }

        @Test
        void shouldStartSessionWithoutPomodoroAndTaskWhenIdsAreNull() {
            var user = createUser();
            var request = new StartPomodoroSessionRequest(
                    null, null, PomodoroType.SHORT_BREAK, 5
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.save(any(PomodoroSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroSessionResponse result = pomodoroSessionService.start(request);

            assertThat(result.type()).isEqualTo(PomodoroType.SHORT_BREAK);
            assertThat(result.pomodoroId()).isNull();
            assertThat(result.taskId()).isNull();
            assertThat(result.taskTitle()).isNull();
            verify(pomodoroSessionRepository).save(sessionCaptor.capture());
            assertThat(sessionCaptor.getValue().getPomodoro()).isNull();
            assertThat(sessionCaptor.getValue().getTask()).isNull();
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnListOfSessionsWhenSessionsExist() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.COMPLETED);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByUserEmailOrderByStartedAtDesc(USER_EMAIL))
                    .thenReturn(List.of(session));

            List<PomodoroSessionResponse> result = pomodoroSessionService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo(PomodoroSessionStatus.COMPLETED);
            verify(pomodoroSessionRepository).findByUserEmailOrderByStartedAtDesc(USER_EMAIL);
        }

        @Test
        void shouldReturnEmptyListWhenNoSessionsExist() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByUserEmailOrderByStartedAtDesc(USER_EMAIL))
                    .thenReturn(List.of());

            List<PomodoroSessionResponse> result = pomodoroSessionService.findAll();

            assertThat(result).isEmpty();
            verify(pomodoroSessionRepository).findByUserEmailOrderByStartedAtDesc(USER_EMAIL);
        }
    }

    @Nested
    class FindById {

        @Test
        void shouldReturnSessionResponseWhenSessionExists() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.IN_PROGRESS);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));

            PomodoroSessionResponse result = pomodoroSessionService.findById(SESSION_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(SESSION_ID);
            verify(pomodoroSessionRepository).findByIdAndUserEmail(SESSION_ID, USER_EMAIL);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenSessionNotFound() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> pomodoroSessionService.findById(SESSION_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Pomodoro session not found");

            verify(pomodoroSessionRepository).findByIdAndUserEmail(SESSION_ID, USER_EMAIL);
        }
    }

    @Nested
    class Complete {

        @Test
        void shouldCompleteFocusSessionAndAwardXp() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.IN_PROGRESS);
            session.setType(PomodoroType.FOCUS);
            var request = new CompletePomodoroSessionRequest(22, "Good session");

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));
            when(pomodoroSessionRepository.save(any(PomodoroSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroSessionResponse result = pomodoroSessionService.complete(SESSION_ID, request);

            assertThat(result.status()).isEqualTo(PomodoroSessionStatus.COMPLETED);
            assertThat(result.actualDurationMinutes()).isEqualTo(22);
            assertThat(result.notes()).isEqualTo("Good session");
            assertThat(result.completedAt()).isNotNull();
            verify(playerProgressService).awardFocusSessionXp(user, 25);
            verify(pomodoroSessionRepository).save(sessionCaptor.capture());
            assertThat(sessionCaptor.getValue().getStatus()).isEqualTo(PomodoroSessionStatus.COMPLETED);
            assertThat(sessionCaptor.getValue().getActualDurationMinutes()).isEqualTo(22);
            assertThat(sessionCaptor.getValue().getNotes()).isEqualTo("Good session");
        }

        @Test
        void shouldCompleteNonFocusSessionWithoutAwardingXp() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.IN_PROGRESS);
            session.setType(PomodoroType.SHORT_BREAK);
            var request = new CompletePomodoroSessionRequest(5, null);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));
            when(pomodoroSessionRepository.save(any(PomodoroSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroSessionResponse result = pomodoroSessionService.complete(SESSION_ID, request);

            assertThat(result.status()).isEqualTo(PomodoroSessionStatus.COMPLETED);
            assertThat(result.actualDurationMinutes()).isEqualTo(5);
            verify(playerProgressService, never()).awardFocusSessionXp(any(), anyInt());
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenSessionIsNotInProgress() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.COMPLETED);
            var request = new CompletePomodoroSessionRequest(22, null);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));

            assertThatThrownBy(() -> pomodoroSessionService.complete(SESSION_ID, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Only in-progress sessions can change lifecycle state");

            verify(pomodoroSessionRepository, never()).save(any());
            verify(playerProgressService, never()).awardFocusSessionXp(any(), anyInt());
        }
    }

    @Nested
    class Cancel {

        @Test
        void shouldCancelSessionWhenSessionIsInProgress() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.IN_PROGRESS);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));
            when(pomodoroSessionRepository.save(any(PomodoroSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroSessionResponse result = pomodoroSessionService.cancel(SESSION_ID);

            assertThat(result.status()).isEqualTo(PomodoroSessionStatus.CANCELLED);
            assertThat(result.completedAt()).isNotNull();
            verify(pomodoroSessionRepository).save(sessionCaptor.capture());
            assertThat(sessionCaptor.getValue().getStatus()).isEqualTo(PomodoroSessionStatus.CANCELLED);
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenSessionIsNotInProgress() {
            var user = createUser();
            var session = createSession(user, PomodoroSessionStatus.CANCELLED);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroSessionRepository.findByIdAndUserEmail(SESSION_ID, USER_EMAIL))
                    .thenReturn(Optional.of(session));

            assertThatThrownBy(() -> pomodoroSessionService.cancel(SESSION_ID))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Only in-progress sessions can change lifecycle state");

            verify(pomodoroSessionRepository, never()).save(any());
        }
    }
}
