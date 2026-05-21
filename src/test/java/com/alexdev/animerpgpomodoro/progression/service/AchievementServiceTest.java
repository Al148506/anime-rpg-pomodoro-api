package com.alexdev.animerpgpomodoro.progression.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementResponse;
import com.alexdev.animerpgpomodoro.progression.dto.AchievementUnlockRequest;
import com.alexdev.animerpgpomodoro.progression.entity.Achievement;
import com.alexdev.animerpgpomodoro.progression.entity.AchievementType;
import com.alexdev.animerpgpomodoro.progression.repository.AchievementRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AchievementService achievementService;

    @Captor
    private ArgumentCaptor<Achievement> achievementCaptor;

    private static final String USER_ID = "user-uuid";
    private static final String USER_EMAIL = "user@example.com";
    private static final String ACHIEVEMENT_CODE = "first_focus";
    private static final String ACHIEVEMENT_TITLE = "First Focus";
    private static final int XP_REWARD = 100;

    private User createUser() {
        return User.builder()
                .id(USER_ID)
                .username("testUser")
                .email(USER_EMAIL)
                .password("encoded")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Achievement createAchievement(User user) {
        return Achievement.builder()
                .id("achievement-uuid")
                .user(user)
                .code(ACHIEVEMENT_CODE)
                .title(ACHIEVEMENT_TITLE)
                .description("Completed your first focus session")
                .type(AchievementType.FOCUS)
                .xpReward(XP_REWARD)
                .unlockedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class FindAllUnlocked {

        @Test
        void shouldReturnListOfAchievementsWhenTheyExist() {
            var user = createUser();
            var achievement = createAchievement(user);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(achievementRepository.findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL))
                    .thenReturn(List.of(achievement));

            List<AchievementResponse> result = achievementService.findAllUnlocked();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).code()).isEqualTo(ACHIEVEMENT_CODE);
            assertThat(result.get(0).title()).isEqualTo(ACHIEVEMENT_TITLE);
            assertThat(result.get(0).type()).isEqualTo(AchievementType.FOCUS);
            assertThat(result.get(0).xpReward()).isEqualTo(XP_REWARD);
            verify(achievementRepository).findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL);
        }

        @Test
        void shouldReturnEmptyListWhenNoAchievementsExist() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(achievementRepository.findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL))
                    .thenReturn(List.of());

            List<AchievementResponse> result = achievementService.findAllUnlocked();

            assertThat(result).isEmpty();
            verify(achievementRepository).findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL);
        }
    }

    @Nested
    class Unlock {

        @Test
        void shouldSaveAndReturnAchievementResponseWhenCodeIsUnique() {
            var user = createUser();
            var request = new AchievementUnlockRequest(
                    ACHIEVEMENT_CODE, ACHIEVEMENT_TITLE,
                    "Completed your first focus session",
                    AchievementType.FOCUS, XP_REWARD
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(achievementRepository.existsByUserEmailAndCode(USER_EMAIL, ACHIEVEMENT_CODE))
                    .thenReturn(false);
            when(achievementRepository.save(any(Achievement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AchievementResponse result = achievementService.unlock(request);

            assertThat(result.code()).isEqualTo(ACHIEVEMENT_CODE);
            assertThat(result.title()).isEqualTo(ACHIEVEMENT_TITLE);
            assertThat(result.type()).isEqualTo(AchievementType.FOCUS);
            assertThat(result.xpReward()).isEqualTo(XP_REWARD);
            verify(achievementRepository).save(achievementCaptor.capture());
            var saved = achievementCaptor.getValue();
            assertThat(saved.getCode()).isEqualTo(ACHIEVEMENT_CODE);
            assertThat(saved.getTitle()).isEqualTo(ACHIEVEMENT_TITLE);
            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.getUnlockedAt()).isNotNull();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenCodeAlreadyExists() {
            var user = createUser();
            var request = new AchievementUnlockRequest(
                    ACHIEVEMENT_CODE, ACHIEVEMENT_TITLE,
                    "Description", AchievementType.FOCUS, XP_REWARD
            );

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(achievementRepository.existsByUserEmailAndCode(USER_EMAIL, ACHIEVEMENT_CODE))
                    .thenReturn(true);

            assertThatThrownBy(() -> achievementService.unlock(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Achievement already unlocked");

            verify(achievementRepository).existsByUserEmailAndCode(USER_EMAIL, ACHIEVEMENT_CODE);
            verify(achievementRepository, never()).save(any());
        }
    }
}
