package com.alexdev.animerpgpomodoro.progression.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.alexdev.animerpgpomodoro.progression.dto.PlayerProgressResponse;
import com.alexdev.animerpgpomodoro.progression.service.PlayerProgressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlayerProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private PlayerProgressService playerProgressService;

    private final PlayerProgressResponse progressResponse = new PlayerProgressResponse(
            "progress-uuid", 3, 250, 10, 2,
            LocalDateTime.now(), LocalDateTime.now()
    );

    @Test
    @DisplayName("Should get current user progress successfully")
    void shouldGetCurrentUserProgressSuccessfully() throws Exception {
        when(playerProgressService.getCurrentUserProgress()).thenReturn(progressResponse);

        mockMvc.perform(get("/api/v1/progression/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("progress-uuid"))
                .andExpect(jsonPath("$.level").value(3))
                .andExpect(jsonPath("$.totalXp").value(250))
                .andExpect(jsonPath("$.completedFocusSessions").value(10))
                .andExpect(jsonPath("$.currentStreakDays").value(2));

        verify(playerProgressService, times(1)).getCurrentUserProgress();
    }
}
