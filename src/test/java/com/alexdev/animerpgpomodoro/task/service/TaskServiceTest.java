package com.alexdev.animerpgpomodoro.task.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.auth.repository.UserRepository;
import com.alexdev.animerpgpomodoro.category.entity.Category;
import com.alexdev.animerpgpomodoro.category.repository.CategoryRepository;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.task.dto.TaskRequest;
import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private static final String USER_EMAIL = "user@example.com";
    private static final String USER_ID = "user-uuid";
    private static final String CATEGORY_ID = "category-uuid";
    private static final String CATEGORY_NAME = "Work";
    private static final String TASK_ID = "task-uuid";

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

    private Category createCategory() {
        return Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .slug("work")
                .build();
    }

    private Task createTask(User user, Category category) {
        return Task.builder()
                .id(TASK_ID)
                .title("Test Task")
                .description("Description")
                .completed(false)
                .priority(TaskPriority.HIGH)
                .xpReward(50)
                .pomodoroSessions(4)
                .dueDate(null)
                .category(category)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MockedStatic<SecurityContextHolder> mockSecurityContext(String email) {
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        var securityContextHolderMock = mockStatic(SecurityContextHolder.class);

        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        return securityContextHolderMock;
    }

    @Nested
    class CreateTask {

        @Test
        void shouldSaveAndReturnTaskResponseWhenDataIsValid() {
            var user = createUser();
            var category = createCategory();
            var request = new TaskRequest(
                    "Test Task", "Description", TaskPriority.HIGH, 50, 4, null, CATEGORY_ID
            );

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
                when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

                TaskResponse result = taskService.createTask(request);

                verify(taskRepository).save(taskCaptor.capture());
                Task savedTask = taskCaptor.getValue();

                assertThat(savedTask.getTitle()).isEqualTo("Test Task");
                assertThat(savedTask.getDescription()).isEqualTo("Description");
                assertThat(savedTask.isCompleted()).isFalse();
                assertThat(savedTask.getPriority()).isEqualTo(TaskPriority.HIGH);
                assertThat(savedTask.getXpReward()).isEqualTo(50);
                assertThat(savedTask.getPomodoroSessions()).isEqualTo(4);
                assertThat(savedTask.getDueDate()).isNull();
                assertThat(savedTask.getCategory()).isEqualTo(category);
                assertThat(savedTask.getUser()).isEqualTo(user);
                assertThat(savedTask.getCreatedAt()).isNotNull();
                assertThat(savedTask.getUpdatedAt()).isNotNull();

                assertThat(result.title()).isEqualTo("Test Task");
                assertThat(result.priority()).isEqualTo(TaskPriority.HIGH);
                assertThat(result.categoryId()).isEqualTo(CATEGORY_ID);
                assertThat(result.categoryName()).isEqualTo(CATEGORY_NAME);
                assertThat(result.completed()).isFalse();
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
            var user = createUser();
            var request = new TaskRequest(
                    "Test Task", "Description", TaskPriority.HIGH, 50, 4, null, "invalid-category"
            );

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(categoryRepository.findById("invalid-category")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.createTask(request))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Category not found");

                verify(categoryRepository).findById("invalid-category");
                verify(taskRepository, never()).save(any());
            }
        }
    }

    @Nested
    class GetAllTasks {

        @Test
        void shouldReturnListOfTasksWhenTasksExist() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of(task));

                List<TaskResponse> result = taskService.getAllTasks();

                assertThat(result).hasSize(1);
                assertThat(result.get(0).id()).isEqualTo(TASK_ID);
                assertThat(result.get(0).title()).isEqualTo("Test Task");
                verify(taskRepository).findByUserEmail(USER_EMAIL);
            }
        }

        @Test
        void shouldReturnEmptyListWhenNoTasksExist() {
            var user = createUser();

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of());

                List<TaskResponse> result = taskService.getAllTasks();

                assertThat(result).isEmpty();
                verify(taskRepository).findByUserEmail(USER_EMAIL);
            }
        }
    }

    @Nested
    class GetCompletedTasks {

        @Test
        void shouldReturnCompletedTasksWhenTheyExist() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmailAndCompletedTrue(USER_EMAIL)).thenReturn(List.of(task));

                List<TaskResponse> result = taskService.getCompletedTasks();

                assertThat(result).hasSize(1);
                verify(taskRepository).findByUserEmailAndCompletedTrue(USER_EMAIL);
            }
        }
    }

    @Nested
    class GetPendingTasks {

        @Test
        void shouldReturnPendingTasksWhenTheyExist() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmailAndCompletedFalse(USER_EMAIL)).thenReturn(List.of(task));

                List<TaskResponse> result = taskService.getPendingTasks();

                assertThat(result).hasSize(1);
                verify(taskRepository).findByUserEmailAndCompletedFalse(USER_EMAIL);
            }
        }
    }

    @Nested
    class GetTasksByPriority {

        @Test
        void shouldReturnFilteredTasksByPriority() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmailAndPriority(USER_EMAIL, TaskPriority.HIGH))
                        .thenReturn(List.of(task));

                List<TaskResponse> result = taskService.getTasksByPriority(TaskPriority.HIGH);

                assertThat(result).hasSize(1);
                verify(taskRepository).findByUserEmailAndPriority(USER_EMAIL, TaskPriority.HIGH);
            }
        }
    }

    @Nested
    class GetTasksByCategory {

        @Test
        void shouldReturnFilteredTasksByCategory() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByUserEmailAndCategoryId(USER_EMAIL, CATEGORY_ID))
                        .thenReturn(List.of(task));

                List<TaskResponse> result = taskService.getTasksByCategory(CATEGORY_ID);

                assertThat(result).hasSize(1);
                verify(taskRepository).findByUserEmailAndCategoryId(USER_EMAIL, CATEGORY_ID);
            }
        }
    }

    @Nested
    class GetTaskById {

        @Test
        void shouldReturnTaskResponseWhenTaskExists() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));

                TaskResponse result = taskService.getTaskById(TASK_ID);

                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(TASK_ID);
                verify(taskRepository).findByIdAndUserEmail(TASK_ID, USER_EMAIL);
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
            var user = createUser();

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.getTaskById(TASK_ID))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Task not found");

                verify(taskRepository).findByIdAndUserEmail(TASK_ID, USER_EMAIL);
            }
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void shouldUpdateAndReturnTaskResponseWhenDataIsValid() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);
            var request = new TaskRequest(
                    "Updated Title", "Updated Desc", TaskPriority.LOW, 10, 2, null, CATEGORY_ID
            );

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
                when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
                when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

                TaskResponse result = taskService.updateTask(TASK_ID, request);

                assertThat(result.title()).isEqualTo("Updated Title");
                assertThat(result.description()).isEqualTo("Updated Desc");
                assertThat(result.priority()).isEqualTo(TaskPriority.LOW);
                assertThat(result.xpReward()).isEqualTo(10);
                verify(taskRepository).save(taskCaptor.capture());
                assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Updated Title");
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
            var user = createUser();
            var request = new TaskRequest(
                    "Updated Title", "Updated Desc", TaskPriority.LOW, 10, 2, null, CATEGORY_ID
            );

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.updateTask(TASK_ID, request))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Task not found");

                verify(taskRepository).findByIdAndUserEmail(TASK_ID, USER_EMAIL);
                verify(taskRepository, never()).save(any());
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);
            var request = new TaskRequest(
                    "Updated Title", "Updated Desc", TaskPriority.LOW, 10, 2, null, "invalid-category"
            );

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
                when(categoryRepository.findById("invalid-category")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.updateTask(TASK_ID, request))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Category not found");

                verify(categoryRepository).findById("invalid-category");
                verify(taskRepository, never()).save(any());
            }
        }
    }

    @Nested
    class ToggleTaskCompletion {

        @Test
        void shouldToggleTaskCompletionWhenTaskWasCompleted() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);
            task.setCompleted(true);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
                when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

                TaskResponse result = taskService.toggleTaskCompletion(TASK_ID);

                assertThat(result.completed()).isFalse();
                verify(taskRepository).save(taskCaptor.capture());
                assertThat(taskCaptor.getValue().isCompleted()).isFalse();
            }
        }

        @Test
        void shouldToggleTaskCompletionWhenTaskWasNotCompleted() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));
                when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

                TaskResponse result = taskService.toggleTaskCompletion(TASK_ID);

                assertThat(result.completed()).isTrue();
                verify(taskRepository).save(taskCaptor.capture());
                assertThat(taskCaptor.getValue().isCompleted()).isTrue();
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
            var user = createUser();

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.toggleTaskCompletion(TASK_ID))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Task not found");

                verify(taskRepository).findByIdAndUserEmail(TASK_ID, USER_EMAIL);
                verify(taskRepository, never()).save(any());
            }
        }
    }

    @Nested
    class DeleteTask {

        @Test
        void shouldDeleteTaskWhenTaskExists() {
            var user = createUser();
            var category = createCategory();
            var task = createTask(user, category);

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.of(task));

                taskService.deleteTask(TASK_ID);

                verify(taskRepository).delete(task);
            }
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
            var user = createUser();

            try (var ctx = mockSecurityContext(USER_EMAIL)) {
                when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
                when(taskRepository.findByIdAndUserEmail(TASK_ID, USER_EMAIL)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> taskService.deleteTask(TASK_ID))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Task not found");

                verify(taskRepository).findByIdAndUserEmail(TASK_ID, USER_EMAIL);
                verify(taskRepository, never()).delete(any());
            }
        }
    }
}
