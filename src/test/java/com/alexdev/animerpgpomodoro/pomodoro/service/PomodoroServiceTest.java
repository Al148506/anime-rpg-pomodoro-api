package com.alexdev.animerpgpomodoro.pomodoro.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroRequest;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroResponse;
import com.alexdev.animerpgpomodoro.pomodoro.dto.RepetitionSettingsRequest;
import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.pomodoro.entity.RepetitionSettings;
import com.alexdev.animerpgpomodoro.pomodoro.repository.PomodoroRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PomodoroServiceTest {

    @Mock
    private PomodoroRepository pomodoroRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PomodoroService pomodoroService;

    @Captor
    private ArgumentCaptor<Pomodoro> pomodoroCaptor;

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

    private Pomodoro createPomodoro(User user, Task task) {
        return Pomodoro.builder()
                .id(POMODORO_ID)
                .name("Focus Session")
                .type(PomodoroType.FOCUS)
                .durationMinutes(25)
                .shortBreakMinutes(5)
                .longBreakMinutes(15)
                .sessionsBeforeLongBreak(4)
                .task(task)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class Create {

        @Test
        void shouldCreateAndReturnPomodoroResponseWhenDataIsValid() {
            var user = createUser();
            var task = createTask(user);
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, TASK_ID, null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroResponse result = pomodoroService.create(request);

            assertThat(result.name()).isEqualTo("Focus Session");
            assertThat(result.type()).isEqualTo(PomodoroType.FOCUS);
            assertThat(result.taskId()).isEqualTo(TASK_ID);
            assertThat(result.taskTitle()).isEqualTo("Test Task");
            assertThat(result.repetitionSettings()).isNull();
            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            assertThat(pomodoroCaptor.getValue().getRepetitionSettings()).isNull();
        }

        @Test
        void shouldCreatePomodoroWithoutTaskWhenTaskIdIsNull() {
            var user = createUser();
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, null, null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroResponse result = pomodoroService.create(request);

            assertThat(result.taskId()).isNull();
            assertThat(result.taskTitle()).isNull();
            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            assertThat(pomodoroCaptor.getValue().getTask()).isNull();
        }

        @Test
        void shouldCreatePomodoroWithRepetitionSettingsWhenProvided() {
            var user = createUser();
            var repetitionSettings = new RepetitionSettingsRequest(true, 5, true);
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, null, repetitionSettings
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroResponse result = pomodoroService.create(request);

            assertThat(result.repetitionSettings()).isNotNull();
            assertThat(result.repetitionSettings().enabled()).isTrue();
            assertThat(result.repetitionSettings().repeatCount()).isEqualTo(5);
            assertThat(result.repetitionSettings().repeatDaily()).isTrue();
            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            var settings = pomodoroCaptor.getValue().getRepetitionSettings();
            assertThat(settings).isNotNull();
            assertThat(settings.isEnabled()).isTrue();
            assertThat(settings.getRepeatCount()).isEqualTo(5);
            assertThat(settings.isRepeatDaily()).isTrue();
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
            var user = createUser();
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, "invalid-task", null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserEmail("invalid-task", USER_EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pomodoroService.create(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Task not found");

            verify(pomodoroRepository, never()).save(any());
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnListOfPomodorosWhenPomodorosExist() {
            var user = createUser();
            var task = createTask(user);
            var pomodoro = createPomodoro(user, task);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of(pomodoro));

            List<PomodoroResponse> result = pomodoroService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Focus Session");
            verify(pomodoroRepository).findByUserEmail(USER_EMAIL);
        }

        @Test
        void shouldReturnEmptyListWhenNoPomodorosExist() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of());

            List<PomodoroResponse> result = pomodoroService.findAll();

            assertThat(result).isEmpty();
            verify(pomodoroRepository).findByUserEmail(USER_EMAIL);
        }
    }

    @Nested
    class FindById {

        @Test
        void shouldReturnPomodoroResponseWhenPomodoroExists() {
            var user = createUser();
            var task = createTask(user);
            var pomodoro = createPomodoro(user, task);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.of(pomodoro));

            PomodoroResponse result = pomodoroService.findById(POMODORO_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(POMODORO_ID);
            verify(pomodoroRepository).findByIdAndUserEmail(POMODORO_ID, USER_EMAIL);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenPomodoroNotFound() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pomodoroService.findById(POMODORO_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Pomodoro not found");

            verify(pomodoroRepository).findByIdAndUserEmail(POMODORO_ID, USER_EMAIL);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateAndReturnPomodoroResponseWhenDataIsValid() {
            var user = createUser();
            var task = createTask(user);
            var pomodoro = createPomodoro(user, task);
            var request = new PomodoroRequest(
                    "Updated Focus", PomodoroType.FOCUS, 30, 5, 15, 4, null, null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.of(pomodoro));
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PomodoroResponse result = pomodoroService.update(POMODORO_ID, request);

            assertThat(result.name()).isEqualTo("Updated Focus");
            assertThat(result.durationMinutes()).isEqualTo(30);
            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            assertThat(pomodoroCaptor.getValue().getName()).isEqualTo("Updated Focus");
            assertThat(pomodoroCaptor.getValue().getDurationMinutes()).isEqualTo(30);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenPomodoroNotFound() {
            var user = createUser();
            var request = new PomodoroRequest(
                    "Updated Focus", PomodoroType.FOCUS, 30, 5, 15, 4, null, null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pomodoroService.update(POMODORO_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Pomodoro not found");

            verify(pomodoroRepository).findByIdAndUserEmail(POMODORO_ID, USER_EMAIL);
            verify(pomodoroRepository, never()).save(any());
        }

        @Test
        void shouldUpdateRepetitionSettingsWhenProvided() {
            var user = createUser();
            var pomodoro = createPomodoro(user, null);
            var repetitionSettings = new RepetitionSettingsRequest(false, 3, false);
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, null, repetitionSettings
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.of(pomodoro));
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            pomodoroService.update(POMODORO_ID, request);

            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            var settings = pomodoroCaptor.getValue().getRepetitionSettings();
            assertThat(settings).isNotNull();
            assertThat(settings.isEnabled()).isFalse();
            assertThat(settings.getRepeatCount()).isEqualTo(3);
            assertThat(settings.isRepeatDaily()).isFalse();
        }

        @Test
        void shouldRemoveRepetitionSettingsWhenRequestIsNull() {
            var user = createUser();
            var pomodoro = createPomodoro(user, null);
            pomodoro.setRepetitionSettings(RepetitionSettings.builder()
                    .pomodoro(pomodoro)
                    .enabled(true)
                    .repeatCount(5)
                    .repeatDaily(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            var request = new PomodoroRequest(
                    "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, null, null
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.of(pomodoro));
            when(pomodoroRepository.save(any(Pomodoro.class))).thenAnswer(invocation -> invocation.getArgument(0));

            pomodoroService.update(POMODORO_ID, request);

            verify(pomodoroRepository).save(pomodoroCaptor.capture());
            assertThat(pomodoroCaptor.getValue().getRepetitionSettings()).isNull();
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeletePomodoroWhenPomodoroExists() {
            var user = createUser();
            var task = createTask(user);
            var pomodoro = createPomodoro(user, task);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.of(pomodoro));

            pomodoroService.delete(POMODORO_ID);

            verify(pomodoroRepository).delete(pomodoro);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenPomodoroNotFound() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(pomodoroRepository.findByIdAndUserEmail(POMODORO_ID, USER_EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pomodoroService.delete(POMODORO_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Pomodoro not found");

            verify(pomodoroRepository).findByIdAndUserEmail(POMODORO_ID, USER_EMAIL);
            verify(pomodoroRepository, never()).delete(any());
        }
    }
}
