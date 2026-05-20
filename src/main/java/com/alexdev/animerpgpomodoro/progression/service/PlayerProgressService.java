package com.alexdev.animerpgpomodoro.progression.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.progression.dto.PlayerProgressResponse;
import com.alexdev.animerpgpomodoro.progression.entity.PlayerProgress;
import com.alexdev.animerpgpomodoro.progression.mapper.PlayerProgressMapper;
import com.alexdev.animerpgpomodoro.progression.repository.PlayerProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PlayerProgressService {

    private static final int XP_PER_LEVEL = 100;

    private final PlayerProgressRepository playerProgressRepository;
    private final CurrentUserService currentUserService;

    public PlayerProgressService(
            PlayerProgressRepository playerProgressRepository,
            CurrentUserService currentUserService
    ) {
        this.playerProgressRepository = playerProgressRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PlayerProgressResponse getCurrentUserProgress() {
        User user = currentUserService.getAuthenticatedUser();
        return PlayerProgressMapper.toResponse(getOrCreateProgress(user));
    }

    @Transactional
    public PlayerProgress awardFocusSessionXp(User user, int xpAmount) {
        PlayerProgress progress = getOrCreateProgress(user);
        int totalXp = progress.getTotalXp() + xpAmount;

        progress.setTotalXp(totalXp);
        progress.setLevel(calculateLevel(totalXp));
        progress.setCompletedFocusSessions(progress.getCompletedFocusSessions() + 1);
        progress.setUpdatedAt(LocalDateTime.now());

        return playerProgressRepository.save(progress);
    }

    private PlayerProgress getOrCreateProgress(User user) {
        return playerProgressRepository.findByUserEmail(user.getEmail())
                .orElseGet(() -> createProgress(user));
    }

    private PlayerProgress createProgress(User user) {
        LocalDateTime now = LocalDateTime.now();

        return playerProgressRepository.save(PlayerProgress.builder()
                .user(user)
                .level(1)
                .totalXp(0)
                .completedFocusSessions(0)
                .currentStreakDays(0)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private int calculateLevel(int totalXp) {
        return totalXp / XP_PER_LEVEL + 1;
    }
}
