package com.alexdev.animerpgpomodoro.task.controller;

import com.alexdev.animerpgpomodoro.task.dto.TaskRequest;
import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import com.alexdev.animerpgpomodoro.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request
    ) {
        return taskService.createTask(request);
    }

    @GetMapping("/completed")
    public List<TaskResponse> getCompletedTasks() {
        return taskService.getCompletedTasks();
    }

    @GetMapping("/pending")
    public List<TaskResponse> getPendingTasks() {
        return taskService.getPendingTasks();
    }

    @GetMapping("/priority/{priority}")
    public List<TaskResponse> getTasksByPriority(
            @PathVariable TaskPriority priority
    ) {
        return taskService.getTasksByPriority(priority);
    }

    @GetMapping("/category/{categoryId}")
    public List<TaskResponse> getTasksByCategory(
            @PathVariable String categoryId
    ) {
        return taskService.getTasksByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable String id
    ) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @PatchMapping("/{id}/toggle")
    public TaskResponse toggleTaskCompletion(
            @PathVariable String id
    ) {
        return taskService.toggleTaskCompletion(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable String id
    ) {
        taskService.deleteTask(id);
    }
}