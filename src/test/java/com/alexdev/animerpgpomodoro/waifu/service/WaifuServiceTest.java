package com.alexdev.animerpgpomodoro.waifu.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuResponse;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockResponse;
import com.alexdev.animerpgpomodoro.waifu.entity.Waifu;
import com.alexdev.animerpgpomodoro.waifu.entity.WaifuRarity;
import com.alexdev.animerpgpomodoro.waifu.entity.WaifuSkinUnlock;
import com.alexdev.animerpgpomodoro.waifu.repository.WaifuRepository;
import com.alexdev.animerpgpomodoro.waifu.repository.WaifuSkinUnlockRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaifuServiceTest {

    @Mock
    private WaifuRepository waifuRepository;

    @Mock
    private WaifuSkinUnlockRepository waifuSkinUnlockRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private WaifuService waifuService;

    @Captor
    private ArgumentCaptor<Waifu> waifuCaptor;

    @Captor
    private ArgumentCaptor<WaifuSkinUnlock> unlockCaptor;

    private static final String WAIFU_ID = "waifu-uuid";
    private static final String WAIFU_NAME = "Miyuki";
    private static final String SKIN_CODE = "default";
    private static final String USER_ID = "user-uuid";
    private static final String USER_EMAIL = "user@example.com";

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

    private Waifu createWaifu() {
        return Waifu.builder()
                .id(WAIFU_ID)
                .name(WAIFU_NAME)
                .description("A kind-hearted student council president")
                .defaultSkinCode(SKIN_CODE)
                .rarity(WaifuRarity.EPIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private WaifuSkinUnlock createSkinUnlock(User user, Waifu waifu) {
        return WaifuSkinUnlock.builder()
                .id("unlock-uuid")
                .user(user)
                .waifu(waifu)
                .skinCode("summer_dress")
                .skinName("Summer Dress")
                .unlockSource("level_5_reward")
                .unlockedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class Create {

        @Test
        void shouldSaveAndReturnWaifuResponseWhenNameIsUnique() {
            var request = new WaifuRequest(WAIFU_NAME, "Description", SKIN_CODE, WaifuRarity.EPIC);
            var waifu = createWaifu();

            when(waifuRepository.existsByName(WAIFU_NAME)).thenReturn(false);
            when(waifuRepository.save(any(Waifu.class))).thenReturn(waifu);

            WaifuResponse result = waifuService.create(request);

            assertThat(result.id()).isEqualTo(WAIFU_ID);
            assertThat(result.name()).isEqualTo(WAIFU_NAME);
            assertThat(result.rarity()).isEqualTo(WaifuRarity.EPIC);
            assertThat(result.defaultSkinCode()).isEqualTo(SKIN_CODE);
            verify(waifuRepository).existsByName(WAIFU_NAME);
            verify(waifuRepository).save(any(Waifu.class));
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenNameAlreadyExists() {
            var request = new WaifuRequest(WAIFU_NAME, "Description", SKIN_CODE, WaifuRarity.EPIC);

            when(waifuRepository.existsByName(WAIFU_NAME)).thenReturn(true);

            assertThatThrownBy(() -> waifuService.create(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Waifu name already exists");

            verify(waifuRepository).existsByName(WAIFU_NAME);
            verify(waifuRepository, never()).save(any());
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnListOfWaifusWhenTheyExist() {
            var waifu = createWaifu();

            when(waifuRepository.findAll()).thenReturn(List.of(waifu));

            List<WaifuResponse> result = waifuService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo(WAIFU_NAME);
            assertThat(result.get(0).rarity()).isEqualTo(WaifuRarity.EPIC);
            verify(waifuRepository).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoWaifusExist() {
            when(waifuRepository.findAll()).thenReturn(List.of());

            List<WaifuResponse> result = waifuService.findAll();

            assertThat(result).isEmpty();
            verify(waifuRepository).findAll();
        }
    }

    @Nested
    class FindCurrentUserSkinUnlocks {

        @Test
        void shouldReturnListOfSkinUnlocksWhenTheyExist() {
            var user = createUser();
            var waifu = createWaifu();
            var unlock = createSkinUnlock(user, waifu);

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(waifuSkinUnlockRepository.findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL))
                    .thenReturn(List.of(unlock));

            List<WaifuSkinUnlockResponse> result = waifuService.findCurrentUserSkinUnlocks();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).skinCode()).isEqualTo("summer_dress");
            assertThat(result.get(0).skinName()).isEqualTo("Summer Dress");
            assertThat(result.get(0).waifuId()).isEqualTo(WAIFU_ID);
            assertThat(result.get(0).waifuName()).isEqualTo(WAIFU_NAME);
            verify(waifuSkinUnlockRepository).findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL);
        }

        @Test
        void shouldReturnEmptyListWhenNoSkinUnlocksExist() {
            var user = createUser();

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(waifuSkinUnlockRepository.findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL))
                    .thenReturn(List.of());

            List<WaifuSkinUnlockResponse> result = waifuService.findCurrentUserSkinUnlocks();

            assertThat(result).isEmpty();
            verify(waifuSkinUnlockRepository).findByUserEmailOrderByUnlockedAtDesc(USER_EMAIL);
        }
    }

    @Nested
    class UnlockSkin {

        @Test
        void shouldSaveAndReturnSkinUnlockResponseWhenDataIsValid() {
            var user = createUser();
            var waifu = createWaifu();
            var request = new WaifuSkinUnlockRequest("summer_dress", "Summer Dress", "level_5_reward");

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(waifuRepository.findById(WAIFU_ID)).thenReturn(Optional.of(waifu));
            when(waifuSkinUnlockRepository.existsByUserEmailAndWaifu_IdAndSkinCode(
                    USER_EMAIL, WAIFU_ID, "summer_dress"
            )).thenReturn(false);
            when(waifuSkinUnlockRepository.save(any(WaifuSkinUnlock.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            WaifuSkinUnlockResponse result = waifuService.unlockSkin(WAIFU_ID, request);

            assertThat(result.skinCode()).isEqualTo("summer_dress");
            assertThat(result.skinName()).isEqualTo("Summer Dress");
            assertThat(result.unlockSource()).isEqualTo("level_5_reward");
            assertThat(result.waifuId()).isEqualTo(WAIFU_ID);
            assertThat(result.waifuName()).isEqualTo(WAIFU_NAME);
            verify(waifuSkinUnlockRepository).save(unlockCaptor.capture());
            var saved = unlockCaptor.getValue();
            assertThat(saved.getSkinCode()).isEqualTo("summer_dress");
            assertThat(saved.getWaifu()).isEqualTo(waifu);
            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.getUnlockedAt()).isNotNull();
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenWaifuNotFound() {
            var user = createUser();
            var request = new WaifuSkinUnlockRequest("summer_dress", "Summer Dress", "level_5_reward");

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(waifuRepository.findById(WAIFU_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> waifuService.unlockSkin(WAIFU_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Waifu not found");

            verify(waifuRepository).findById(WAIFU_ID);
            verify(waifuSkinUnlockRepository, never()).save(any());
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenSkinAlreadyUnlocked() {
            var user = createUser();
            var waifu = createWaifu();
            var request = new WaifuSkinUnlockRequest("summer_dress", "Summer Dress", "level_5_reward");

            when(currentUserService.getAuthenticatedUser()).thenReturn(user);
            when(waifuRepository.findById(WAIFU_ID)).thenReturn(Optional.of(waifu));
            when(waifuSkinUnlockRepository.existsByUserEmailAndWaifu_IdAndSkinCode(
                    USER_EMAIL, WAIFU_ID, "summer_dress"
            )).thenReturn(true);

            assertThatThrownBy(() -> waifuService.unlockSkin(WAIFU_ID, request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Waifu skin already unlocked");

            verify(waifuSkinUnlockRepository).existsByUserEmailAndWaifu_IdAndSkinCode(
                    USER_EMAIL, WAIFU_ID, "summer_dress"
            );
            verify(waifuSkinUnlockRepository, never()).save(any());
        }
    }
}
