package com.alexdev.animerpgpomodoro.progression.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementResponse;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementUnlockRequest;
import com.alexdev.animerpgpomodoro.progression.entity.Achievement;
import com.alexdev.animerpgpomodoro.progression.mapper.AchievementMapper;
import com.alexdev.animerpgpomodoro.progression.repository.AchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final CurrentUserService currentUserService;

    public AchievementService(
            AchievementRepository achievementRepository,
            CurrentUserService currentUserService
    ) {
        this.achievementRepository = achievementRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> findAllUnlocked() {
        User user = currentUserService.getAuthenticatedUser();

        return achievementRepository.findByUserEmailOrderByUnlockedAtDesc(user.getEmail())
                .stream()
                .map(AchievementMapper::toResponse)
                .toList();
    }

    @Transactional
    public AchievementResponse unlock(AchievementUnlockRequest request) {
        User user = currentUserService.getAuthenticatedUser();

        if (achievementRepository.existsByUserEmailAndCode(user.getEmail(), request.code())) {
            throw new DuplicateResourceException("Achievement already unlocked");
        }

        LocalDateTime now = LocalDateTime.now();

        Achievement achievement = Achievement.builder()
                .user(user)
                .code(request.code())
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .xpReward(request.xpReward())
                .unlockedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return AchievementMapper.toResponse(achievementRepository.save(achievement));
    }
}
