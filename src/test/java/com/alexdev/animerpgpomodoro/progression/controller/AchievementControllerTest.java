package com.alexdev.animerpgpomodoro.progression.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementResponse;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementUnlockRequest;
import com.alexdev.animerpgpomodoro.progression.entity.AchievementType;
import com.alexdev.animerpgpomodoro.progression.service.AchievementService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AchievementController.class)
@AutoConfigureMockMvc(addFilters = false)
class AchievementControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AchievementService achievementService;

    private final AchievementResponse achievementResponse = new AchievementResponse(
            "achievement-uuid", "first_focus", "First Focus",
            "Completed your first focus session", AchievementType.FOCUS,
            100, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
    );

    @Test
    @DisplayName("Should find all unlocked achievements successfully")
    void shouldFindAllUnlockedAchievementsSuccessfully() throws Exception {
        when(achievementService.findAllUnlocked()).thenReturn(List.of(achievementResponse));

        mockMvc.perform(get("/api/v1/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("achievement-uuid"))
                .andExpect(jsonPath("$[0].code").value("first_focus"))
                .andExpect(jsonPath("$[0].title").value("First Focus"))
                .andExpect(jsonPath("$[0].type").value("FOCUS"))
                .andExpect(jsonPath("$[0].xpReward").value(100));

        verify(achievementService, times(1)).findAllUnlocked();
    }

    @Test
    @DisplayName("Should unlock achievement successfully")
    void shouldUnlockAchievementSuccessfully() throws Exception {
        var request = new AchievementUnlockRequest(
                "first_focus", "First Focus",
                "Completed your first focus session",
                AchievementType.FOCUS, 100
        );

        when(achievementService.unlock(any(AchievementUnlockRequest.class)))
                .thenReturn(achievementResponse);

        mockMvc.perform(post("/api/v1/achievements/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("first_focus"))
                .andExpect(jsonPath("$.title").value("First Focus"))
                .andExpect(jsonPath("$.type").value("FOCUS"))
                .andExpect(jsonPath("$.xpReward").value(100));

        verify(achievementService, times(1)).unlock(any(AchievementUnlockRequest.class));
    }
}
