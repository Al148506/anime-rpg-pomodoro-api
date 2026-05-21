package com.alexdev.animerpgpomodoro.progression.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.progression.dto.PlayerProgressResponse;
import com.alexdev.animerpgpomodoro.progression.entity.PlayerProgress;
import com.alexdev.animerpgpomodoro.progression.repository.PlayerProgressRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerProgressServiceTest {

    @Mock
    private PlayerProgressRepository playerProgressRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PlayerProgressService playerProgressService;

    @Captor
    private ArgumentCaptor<PlayerProgress> progressCaptor;

    private static final String USER_ID = "user-uuid";
    private static final String USER_EMAIL = "user@example.com";
    private static final int XP_PER_LEVEL = 100;

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

    private PlayerProgress createProgress(User user, int totalXp, int level, int sessions) {
        return PlayerProgress.builder()
                .id("progress-uuid")
                .user(user)
                .level(level)
                .totalXp(totalXp)
                .completedFocusSessions(sessions)
                .currentStreakDays(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class GetCurrentUserProgress {

        @Test
        void shouldReturnExistingProgressWhenItAlreadyExists() {
            var user = createUser();
            var progress = createProgress(user, 250, 3, 10);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(progress));

            PlayerProgressResponse result = playerProgressService.getCurrentUserProgress();

            assertThat(result.level()).isEqualTo(3);
            assertThat(result.totalXp()).isEqualTo(250);
            assertThat(result.completedFocusSessions()).isEqualTo(10);
            verify(playerProgressRepository).findByUserEmail(USER_EMAIL);
            verify(playerProgressRepository, never()).save(any());
        }

        @Test
        void shouldCreateAndReturnNewProgressWhenItDoesNotExist() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());
            when(playerProgressRepository.save(any(PlayerProgress.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerProgressResponse result = playerProgressService.getCurrentUserProgress();

            assertThat(result.level()).isEqualTo(1);
            assertThat(result.totalXp()).isEqualTo(0);
            assertThat(result.completedFocusSessions()).isEqualTo(0);
            assertThat(result.currentStreakDays()).isEqualTo(0);
            verify(playerProgressRepository).save(progressCaptor.capture());
            var saved = progressCaptor.getValue();
            assertThat(saved.getLevel()).isEqualTo(1);
            assertThat(saved.getTotalXp()).isEqualTo(0);
            assertThat(saved.getUser()).isEqualTo(user);
        }
    }

    @Nested
    class AwardFocusSessionXp {

        @Test
        void shouldAwardXpAndIncrementSessionCount() {
            var user = createUser();
            var progress = createProgress(user, 50, 1, 5);

            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(progress));
            when(playerProgressRepository.save(any(PlayerProgress.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerProgress result = playerProgressService.awardFocusSessionXp(user, 25);

            assertThat(result.getTotalXp()).isEqualTo(75);
            assertThat(result.getLevel()).isEqualTo(1);
            assertThat(result.getCompletedFocusSessions()).isEqualTo(6);
        }

        @Test
        void shouldLevelUpWhenXpExceedsThreshold() {
            var user = createUser();
            var progress = createProgress(user, 90, 1, 5);

            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(progress));
            when(playerProgressRepository.save(any(PlayerProgress.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerProgress result = playerProgressService.awardFocusSessionXp(user, 20);

            assertThat(result.getTotalXp()).isEqualTo(110);
            assertThat(result.getLevel()).isEqualTo(2);
            assertThat(result.getCompletedFocusSessions()).isEqualTo(6);
        }

        @Test
        void shouldLevelUpMultipleTimesWhenLargeXpIsAwarded() {
            var user = createUser();
            var progress = createProgress(user, 50, 1, 5);

            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(progress));
            when(playerProgressRepository.save(any(PlayerProgress.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerProgress result = playerProgressService.awardFocusSessionXp(user, 350);

            assertThat(result.getTotalXp()).isEqualTo(400);
            assertThat(result.getLevel()).isEqualTo(5);
            assertThat(result.getCompletedFocusSessions()).isEqualTo(6);
        }

        @Test
        void shouldCreateProgressWhenNotFoundAndAwardXp() {
            var user = createUser();
            var newProgress = createProgress(user, 0, 1, 0);
            newProgress.setId(null);

            when(playerProgressRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());
            when(playerProgressRepository.save(any(PlayerProgress.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerProgress result = playerProgressService.awardFocusSessionXp(user, 25);

            assertThat(result.getTotalXp()).isEqualTo(25);
            assertThat(result.getLevel()).isEqualTo(1);
            assertThat(result.getCompletedFocusSessions()).isEqualTo(1);
            verify(playerProgressRepository, times(2)).save(any(PlayerProgress.class));
        }
    }
}
