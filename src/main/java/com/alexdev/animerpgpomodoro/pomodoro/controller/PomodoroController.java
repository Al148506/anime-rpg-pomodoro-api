package com.alexdev.animerpgpomodoro.pomodoro.controller;

import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroRequest;
import com.alexdev.animerpgpomodoro.pomodoro.dto.PomodoroResponse;
import com.alexdev.animerpgpomodoro.pomodoro.service.PomodoroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pomodoros")
public class PomodoroController {

    private final PomodoroService pomodoroService;

    public PomodoroController(PomodoroService pomodoroService) {
        this.pomodoroService = pomodoroService;
    }

    @PostMapping
    public ResponseEntity<PomodoroResponse> create(@Valid @RequestBody PomodoroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pomodoroService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PomodoroResponse>> findAll() {
        return ResponseEntity.ok(pomodoroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PomodoroResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(pomodoroService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PomodoroResponse> update(
            @PathVariable String id,
            @Valid @RequestBody PomodoroRequest request
    ) {
        return ResponseEntity.ok(pomodoroService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        pomodoroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
