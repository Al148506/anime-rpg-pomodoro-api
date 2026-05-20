package com.alexdev.animerpgpomodoro.progression.controller;

import com.alexdev.animerpgpomodoro.progression.dto.AchievementResponse;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementUnlockRequest;
import com.alexdev.animerpgpomodoro.progression.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> findAllUnlocked() {
        return ResponseEntity.ok(achievementService.findAllUnlocked());
    }

    @PostMapping("/unlock")
    public ResponseEntity<AchievementResponse> unlock(
            @Valid @RequestBody AchievementUnlockRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(achievementService.unlock(request));
    }
}
