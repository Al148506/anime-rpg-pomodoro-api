package com.alexdev.animerpgpomodoro.waifu.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuResponse;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockResponse;
import com.alexdev.animerpgpomodoro.waifu.entity.WaifuRarity;
import com.alexdev.animerpgpomodoro.waifu.service.WaifuService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaifuController.class)
@AutoConfigureMockMvc(addFilters = false)
class WaifuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private WaifuService waifuService;

    private final WaifuResponse waifuResponse = new WaifuResponse(
            "waifu-uuid", "Miyuki", "A kind-hearted student council president",
            "default", WaifuRarity.EPIC,
            LocalDateTime.now(), LocalDateTime.now()
    );

    private final WaifuSkinUnlockResponse skinUnlockResponse = new WaifuSkinUnlockResponse(
            "unlock-uuid", "waifu-uuid", "Miyuki",
            "summer_dress", "Summer Dress", "level_5_reward",
            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
    );

    @Test
    @DisplayName("Should find all waifus successfully")
    void shouldFindAllWaifusSuccessfully() throws Exception {
        when(waifuService.findAll()).thenReturn(List.of(waifuResponse));

        mockMvc.perform(get("/api/v1/waifus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("waifu-uuid"))
                .andExpect(jsonPath("$[0].name").value("Miyuki"))
                .andExpect(jsonPath("$[0].rarity").value("EPIC"));

        verify(waifuService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create waifu successfully")
    void shouldCreateWaifuSuccessfully() throws Exception {
        var request = new WaifuRequest("Miyuki", "Description", "default", WaifuRarity.EPIC);

        when(waifuService.create(any(WaifuRequest.class))).thenReturn(waifuResponse);

        mockMvc.perform(post("/api/v1/waifus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("waifu-uuid"))
                .andExpect(jsonPath("$.name").value("Miyuki"))
                .andExpect(jsonPath("$.rarity").value("EPIC"));

        verify(waifuService, times(1)).create(any(WaifuRequest.class));
    }

    @Test
    @DisplayName("Should find current user skin unlocks successfully")
    void shouldFindCurrentUserSkinUnlocksSuccessfully() throws Exception {
        when(waifuService.findCurrentUserSkinUnlocks()).thenReturn(List.of(skinUnlockResponse));

        mockMvc.perform(get("/api/v1/waifus/skins/unlocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("unlock-uuid"))
                .andExpect(jsonPath("$[0].skinCode").value("summer_dress"))
                .andExpect(jsonPath("$[0].skinName").value("Summer Dress"))
                .andExpect(jsonPath("$[0].waifuId").value("waifu-uuid"))
                .andExpect(jsonPath("$[0].waifuName").value("Miyuki"));

        verify(waifuService, times(1)).findCurrentUserSkinUnlocks();
    }

    @Test
    @DisplayName("Should unlock skin successfully")
    void shouldUnlockSkinSuccessfully() throws Exception {
        var request = new WaifuSkinUnlockRequest("summer_dress", "Summer Dress", "level_5_reward");

        when(waifuService.unlockSkin(eq("waifu-uuid"), any(WaifuSkinUnlockRequest.class)))
                .thenReturn(skinUnlockResponse);

        mockMvc.perform(post("/api/v1/waifus/waifu-uuid/skins/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skinCode").value("summer_dress"))
                .andExpect(jsonPath("$.skinName").value("Summer Dress"))
                .andExpect(jsonPath("$.waifuId").value("waifu-uuid"));

        verify(waifuService, times(1)).unlockSkin(eq("waifu-uuid"), any(WaifuSkinUnlockRequest.class));
    }
}
