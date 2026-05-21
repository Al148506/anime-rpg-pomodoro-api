package com.alexdev.animerpgpomodoro.task.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.alexdev.animerpgpomodoro.task.dto.TaskRequest;
import com.alexdev.animerpgpomodoro.task.dto.TaskResponse;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import com.alexdev.animerpgpomodoro.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TaskService taskService;

    private final TaskResponse taskResponse = new TaskResponse(
            "task-uuid", "Test Task", "Description", false,
            TaskPriority.HIGH, 50, 4, null, "Work", "category-uuid",
            LocalDateTime.now()
    );

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasksSuccessfully() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-uuid"))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() throws Exception {
        var request = new TaskRequest(
                "Test Task", "Description", TaskPriority.HIGH, 50, 4, null, "category-uuid"
        );

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-uuid"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("Should get completed tasks successfully")
    void shouldGetCompletedTasksSuccessfully() throws Exception {
        when(taskService.getCompletedTasks()).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/v1/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(false));

        verify(taskService, times(1)).getCompletedTasks();
    }

    @Test
    @DisplayName("Should get pending tasks successfully")
    void shouldGetPendingTasksSuccessfully() throws Exception {
        when(taskService.getPendingTasks()).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/v1/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(false));

        verify(taskService, times(1)).getPendingTasks();
    }

    @Test
    @DisplayName("Should get tasks by priority successfully")
    void shouldGetTasksByPrioritySuccessfully() throws Exception {
        when(taskService.getTasksByPriority(TaskPriority.HIGH)).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/v1/tasks/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("HIGH"));

        verify(taskService, times(1)).getTasksByPriority(TaskPriority.HIGH);
    }

    @Test
    @DisplayName("Should get tasks by category successfully")
    void shouldGetTasksByCategorySuccessfully() throws Exception {
        when(taskService.getTasksByCategory("category-uuid")).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/v1/tasks/category/category-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value("category-uuid"));

        verify(taskService, times(1)).getTasksByCategory("category-uuid");
    }

    @Test
    @DisplayName("Should get task by id successfully")
    void shouldGetTaskByIdSuccessfully() throws Exception {
        when(taskService.getTaskById("task-uuid")).thenReturn(taskResponse);

        mockMvc.perform(get("/api/v1/tasks/task-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-uuid"))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).getTaskById("task-uuid");
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() throws Exception {
        var request = new TaskRequest(
                "Updated Title", "Updated Desc", TaskPriority.LOW, 10, 2, null, "category-uuid"
        );
        var updatedResponse = new TaskResponse(
                "task-uuid", "Updated Title", "Updated Desc", false,
                TaskPriority.LOW, 10, 2, null, "Work", "category-uuid",
                LocalDateTime.now()
        );

        when(taskService.updateTask(eq("task-uuid"), any(TaskRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/tasks/task-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.priority").value("LOW"));

        verify(taskService, times(1)).updateTask(eq("task-uuid"), any(TaskRequest.class));
    }

    @Test
    @DisplayName("Should toggle task completion successfully")
    void shouldToggleTaskCompletionSuccessfully() throws Exception {
        var toggledResponse = new TaskResponse(
                "task-uuid", "Test Task", "Description", true,
                TaskPriority.HIGH, 50, 4, null, "Work", "category-uuid",
                LocalDateTime.now()
        );

        when(taskService.toggleTaskCompletion("task-uuid")).thenReturn(toggledResponse);

        mockMvc.perform(patch("/api/v1/tasks/task-uuid/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        verify(taskService, times(1)).toggleTaskCompletion("task-uuid");
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() throws Exception {
        doNothing().when(taskService).deleteTask("task-uuid");

        mockMvc.perform(delete("/api/v1/tasks/task-uuid"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask("task-uuid");
    }
}
