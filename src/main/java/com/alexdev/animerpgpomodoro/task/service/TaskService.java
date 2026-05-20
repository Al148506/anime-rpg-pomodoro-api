package com.alexdev.animerpgpomodoro.task.service;
import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.repository.UserRepository;
import com.alexdev.animerpgpomodoro.category.entity.Category;
import com.alexdev.animerpgpomodoro.category.repository.CategoryRepository;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.task.dto.TaskRequest;
import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import com.alexdev.animerpgpomodoro.task.mapper.TaskMapper;
import com.alexdev.animerpgpomodoro.task.repository.TaskRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    public TaskResponse createTask(TaskRequest request) {

        User user = getAuthenticatedUser();

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found"
                ));

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .completed(false)
                .priority(request.priority())
                .xpReward(request.xpReward())
                .pomodoroSessions(request.pomodoroSessions())
                .dueDate(request.dueDate())
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {

        User user = getAuthenticatedUser();

        return taskRepository.findByUserEmail(user.getEmail())
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getCompletedTasks() {

        User user = getAuthenticatedUser();

        return taskRepository
                .findByUserEmailAndCompletedTrue(user.getEmail())
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getPendingTasks() {

        User user = getAuthenticatedUser();

        return taskRepository
                .findByUserEmailAndCompletedFalse(user.getEmail())
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksByPriority(
            TaskPriority priority
    ) {

        User user = getAuthenticatedUser();

        return taskRepository
                .findByUserEmailAndPriority(
                        user.getEmail(),
                        priority
                )
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksByCategory(
            String categoryId
    ) {

        User user = getAuthenticatedUser();

        return taskRepository
                .findByUserEmailAndCategoryId(
                        user.getEmail(),
                        categoryId
                )
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(String id) {

        User user = getAuthenticatedUser();

        Task task = taskRepository
                .findByIdAndUserEmail(
                        id,
                        user.getEmail()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        return TaskMapper.toResponse(task);
    }

    public TaskResponse updateTask(
            String id,
            TaskRequest request
    ) {

        User user = getAuthenticatedUser();

        Task task = taskRepository
                .findByIdAndUserEmail(
                        id,
                        user.getEmail()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found"
                ));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setXpReward(request.xpReward());
        task.setPomodoroSessions(request.pomodoroSessions());
        task.setDueDate(request.dueDate());
        task.setCategory(category);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.toResponse(updatedTask);
    }

    public TaskResponse toggleTaskCompletion(String id) {

        User user = getAuthenticatedUser();

        Task task = taskRepository
                .findByIdAndUserEmail(
                        id,
                        user.getEmail()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        task.setCompleted(!task.isCompleted());

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.toResponse(updatedTask);
    }

    public void deleteTask(String id) {

        User user = getAuthenticatedUser();

        Task task = taskRepository
                .findByIdAndUserEmail(
                        id,
                        user.getEmail()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        taskRepository.delete(task);
    }
}