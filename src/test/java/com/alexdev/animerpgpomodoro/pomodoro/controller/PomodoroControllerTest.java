package com.alexdev.animerpgpomodoro.pomodoro.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroRequest;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroResponse;
import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.pomodoro.service.PomodoroService;
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

@WebMvcTest(PomodoroController.class)
@AutoConfigureMockMvc(addFilters = false)
class PomodoroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private PomodoroService pomodoroService;

    private final PomodoroResponse pomodoroResponse = new PomodoroResponse(
            "pomodoro-uuid", "Focus Session", PomodoroType.FOCUS,
            25, 5, 15, 4, null, null, null,
            LocalDateTime.now(), LocalDateTime.now()
    );

    @Test
    @DisplayName("Should create pomodoro successfully")
    void shouldCreatePomodoroSuccessfully() throws Exception {
        var request = new PomodoroRequest(
                "Focus Session", PomodoroType.FOCUS, 25, 5, 15, 4, null, null
        );

        when(pomodoroService.create(any(PomodoroRequest.class))).thenReturn(pomodoroResponse);

        mockMvc.perform(post("/api/v1/pomodoros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("pomodoro-uuid"))
                .andExpect(jsonPath("$.name").value("Focus Session"))
                .andExpect(jsonPath("$.type").value("FOCUS"))
                .andExpect(jsonPath("$.durationMinutes").value(25));

        verify(pomodoroService, times(1)).create(any(PomodoroRequest.class));
    }

    @Test
    @DisplayName("Should find all pomodoros successfully")
    void shouldFindAllPomodorosSuccessfully() throws Exception {
        when(pomodoroService.findAll()).thenReturn(List.of(pomodoroResponse));

        mockMvc.perform(get("/api/v1/pomodoros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pomodoro-uuid"))
                .andExpect(jsonPath("$[0].name").value("Focus Session"));

        verify(pomodoroService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find pomodoro by id successfully")
    void shouldFindPomodoroByIdSuccessfully() throws Exception {
        when(pomodoroService.findById("pomodoro-uuid")).thenReturn(pomodoroResponse);

        mockMvc.perform(get("/api/v1/pomodoros/pomodoro-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pomodoro-uuid"))
                .andExpect(jsonPath("$.name").value("Focus Session"));

        verify(pomodoroService, times(1)).findById("pomodoro-uuid");
    }

    @Test
    @DisplayName("Should update pomodoro successfully")
    void shouldUpdatePomodoroSuccessfully() throws Exception {
        var request = new PomodoroRequest(
                "Updated Focus", PomodoroType.FOCUS, 30, 5, 15, 4, null, null
        );
        var updatedResponse = new PomodoroResponse(
                "pomodoro-uuid", "Updated Focus", PomodoroType.FOCUS,
                30, 5, 15, 4, null, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(pomodoroService.update(eq("pomodoro-uuid"), any(PomodoroRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/pomodoros/pomodoro-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Focus"))
                .andExpect(jsonPath("$.durationMinutes").value(30));

        verify(pomodoroService, times(1)).update(eq("pomodoro-uuid"), any(PomodoroRequest.class));
    }

    @Test
    @DisplayName("Should delete pomodoro successfully")
    void shouldDeletePomodoroSuccessfully() throws Exception {
        doNothing().when(pomodoroService).delete("pomodoro-uuid");

        mockMvc.perform(delete("/api/v1/pomodoros/pomodoro-uuid"))
                .andExpect(status().isNoContent());

        verify(pomodoroService, times(1)).delete("pomodoro-uuid");
    }
}
