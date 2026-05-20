package com.alexdev.animerpgpomodoro.task.service;

import com.alexdev.animerpgpomodoro.category.entity.Category;
import com.alexdev.animerpgpomodoro.category.repository.CategoryRepository;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.task.dto.TaskRequest;
import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import com.alexdev.animerpgpomodoro.task.mapper.TaskMapper;
import com.alexdev.animerpgpomodoro.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository
    ) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public TaskResponse createTask(TaskRequest request) {

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
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getCompletedTasks() {

        return taskRepository.findByCompletedTrue()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getPendingTasks() {

        return taskRepository.findByCompletedFalse()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksByPriority(TaskPriority priority) {

        return taskRepository.findByPriority(priority)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksByCategory(String categoryId) {

        return taskRepository.findByCategoryId(categoryId)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(String id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        return TaskMapper.toResponse(task);
    }

    public TaskResponse updateTask(
            String id,
            TaskRequest request
    ) {

        Task task = taskRepository.findById(id)
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

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        task.setCompleted(!task.isCompleted());

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        return TaskMapper.toResponse(updatedTask);
    }

    public void deleteTask(String id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found"
                ));

        taskRepository.delete(task);
    }
}