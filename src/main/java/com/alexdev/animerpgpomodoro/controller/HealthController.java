package com.alexdev.animerpgpomodoro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/v1/health")
    public String health() {
        return "Anime RPG Pomodoro API is running!";
    }
}
