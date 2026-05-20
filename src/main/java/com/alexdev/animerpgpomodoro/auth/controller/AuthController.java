package com.alexdev.animerpgpomodoro.auth.controller;

import com.alexdev.animerpgpomodoro.auth.dto.RegisterRequest;
import com.alexdev.animerpgpomodoro.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.alexdev.animerpgpomodoro.auth.dto.AuthResponse;
import com.alexdev.animerpgpomodoro.auth.dto.LoginRequest;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}