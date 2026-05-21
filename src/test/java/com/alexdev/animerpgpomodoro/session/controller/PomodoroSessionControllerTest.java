package com.alexdev.animerpgpomodoro.session.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.session.dto.CompletePomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.dto.PomodoroSessionResponse;
import com.alexdev.animerpgpomodoro.session.dto.StartPomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.entity.PomodoroSessionStatus;
import com.alexdev.animerpgpomodoro.session.service.PomodoroSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PomodoroSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PomodoroSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PomodoroSessionService pomodoroSessionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final LocalDateTime FIXED_DATE =
            LocalDateTime.of(2026, 5, 21, 10, 0);

    private final PomodoroSessionResponse sessionResponse =
            new PomodoroSessionResponse(
                    "session-uuid",
                    "pomodoro-uuid",
                    "task-uuid",
                    "Test Task",
                    PomodoroType.FOCUS,
                    PomodoroSessionStatus.IN_PROGRESS,
                    25,
                    null,
                    FIXED_DATE,
                    null,
                    null,
                    FIXED_DATE,
                    FIXED_DATE
            );

    @Test
    @DisplayName("Should start session successfully")
    void shouldStartSessionSuccessfully() throws Exception {

        var request = new StartPomodoroSessionRequest(
                "pomodoro-uuid",
                "task-uuid",
                PomodoroType.FOCUS,
                25
        );

        when(pomodoroSessionService.start(any(StartPomodoroSessionRequest.class)))
                .thenReturn(sessionResponse);

        mockMvc.perform(
                        post("/api/v1/sessions/start")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("session-uuid"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.type").value("FOCUS"))
                .andExpect(jsonPath("$.plannedDurationMinutes").value(25));

        verify(pomodoroSessionService, times(1))
                .start(any(StartPomodoroSessionRequest.class));
    }

    @Test
    @DisplayName("Should find all sessions successfully")
    void shouldFindAllSessionsSuccessfully() throws Exception {

        when(pomodoroSessionService.findAll())
                .thenReturn(List.of(sessionResponse));

        mockMvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("session-uuid"));

        verify(pomodoroSessionService, times(1))
                .findAll();
    }

    @Test
    @DisplayName("Should find session by id successfully")
    void shouldFindSessionByIdSuccessfully() throws Exception {

        when(pomodoroSessionService.findById("session-uuid"))
                .thenReturn(sessionResponse);

        mockMvc.perform(get("/api/v1/sessions/session-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("session-uuid"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(pomodoroSessionService, times(1))
                .findById("session-uuid");
    }

    @Test
    @DisplayName("Should complete session successfully")
    void shouldCompleteSessionSuccessfully() throws Exception {

        var completedResponse = new PomodoroSessionResponse(
                "session-uuid",
                "pomodoro-uuid",
                null,
                null,
                PomodoroType.FOCUS,
                PomodoroSessionStatus.COMPLETED,
                25,
                22,
                FIXED_DATE,
                FIXED_DATE,
                "Good job",
                FIXED_DATE,
                FIXED_DATE
        );

        var request = new CompletePomodoroSessionRequest(
                22,
                "Good job"
        );

        when(
                pomodoroSessionService.complete(
                        eq("session-uuid"),
                        any(CompletePomodoroSessionRequest.class)
                )
        ).thenReturn(completedResponse);

        mockMvc.perform(
                        patch("/api/v1/sessions/session-uuid/complete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(pomodoroSessionService, times(1))
                .complete(
                        eq("session-uuid"),
                        any(CompletePomodoroSessionRequest.class)
                );
    }

    @Test
    @DisplayName("Should cancel session successfully")
    void shouldCancelSessionSuccessfully() throws Exception {

        var cancelledResponse = new PomodoroSessionResponse(
                "session-uuid",
                null,
                null,
                null,
                PomodoroType.FOCUS,
                PomodoroSessionStatus.CANCELLED,
                25,
                null,
                FIXED_DATE,
                FIXED_DATE,
                null,
                FIXED_DATE,
                FIXED_DATE
        );

        when(pomodoroSessionService.cancel("session-uuid"))
                .thenReturn(cancelledResponse);

        mockMvc.perform(
                        patch("/api/v1/sessions/session-uuid/cancel")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(pomodoroSessionService, times(1))
                .cancel("session-uuid");
    }
}