package com.alexdev.animerpgpomodoro.progression.controller;

import com.alexdev.animerpgpomodoro.progression.dto.PlayerProgressResponse;
import com.alexdev.animerpgpomodoro.progression.service.PlayerProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progression")
public class PlayerProgressController {

    private final PlayerProgressService playerProgressService;

    public PlayerProgressController(PlayerProgressService playerProgressService) {
        this.playerProgressService = playerProgressService;
    }

    @GetMapping("/me")
    public ResponseEntity<PlayerProgressResponse> getCurrentUserProgress() {
        return ResponseEntity.ok(playerProgressService.getCurrentUserProgress());
    }
}
