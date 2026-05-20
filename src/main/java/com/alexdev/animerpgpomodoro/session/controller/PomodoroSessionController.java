package com.alexdev.animerpgpomodoro.session.controller;

import com.alexdev.animerpgpomodoro.session.dto.CompletePomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.dto.PomodoroSessionResponse;
import com.alexdev.animerpgpomodoro.session.dto.StartPomodoroSessionRequest;
import com.alexdev.animerpgpomodoro.session.service.PomodoroSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
public class PomodoroSessionController {

    private final PomodoroSessionService pomodoroSessionService;

    public PomodoroSessionController(PomodoroSessionService pomodoroSessionService) {
        this.pomodoroSessionService = pomodoroSessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<PomodoroSessionResponse> start(
            @Valid @RequestBody StartPomodoroSessionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pomodoroSessionService.start(request));
    }

    @GetMapping
    public ResponseEntity<List<PomodoroSessionResponse>> findAll() {
        return ResponseEntity.ok(pomodoroSessionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PomodoroSessionResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(pomodoroSessionService.findById(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<PomodoroSessionResponse> complete(
            @PathVariable String id,
            @Valid @RequestBody CompletePomodoroSessionRequest request
    ) {
        return ResponseEntity.ok(pomodoroSessionService.complete(id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PomodoroSessionResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(pomodoroSessionService.cancel(id));
    }
}
