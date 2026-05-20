package com.alexdev.animerpgpomodoro.waifu.service;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import com.alexdev.animerpgpomodoro.common.security.CurrentUserService;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuResponse;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockResponse;
import com.alexdev.animerpgpomodoro.waifu.entity.Waifu;
import com.alexdev.animerpgpomodoro.waifu.entity.WaifuSkinUnlock;
import com.alexdev.animerpgpomodoro.waifu.mapper.WaifuMapper;
import com.alexdev.animerpgpomodoro.waifu.repository.WaifuRepository;
import com.alexdev.animerpgpomodoro.waifu.repository.WaifuSkinUnlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaifuService {

    private final WaifuRepository waifuRepository;
    private final WaifuSkinUnlockRepository waifuSkinUnlockRepository;
    private final CurrentUserService currentUserService;

    public WaifuService(
            WaifuRepository waifuRepository,
            WaifuSkinUnlockRepository waifuSkinUnlockRepository,
            CurrentUserService currentUserService
    ) {
        this.waifuRepository = waifuRepository;
        this.waifuSkinUnlockRepository = waifuSkinUnlockRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public WaifuResponse create(WaifuRequest request) {
        if (waifuRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Waifu name already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        Waifu waifu = Waifu.builder()
                .name(request.name())
                .description(request.description())
                .defaultSkinCode(request.defaultSkinCode())
                .rarity(request.rarity())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return WaifuMapper.toResponse(waifuRepository.save(waifu));
    }

    @Transactional(readOnly = true)
    public List<WaifuResponse> findAll() {
        return waifuRepository.findAll()
                .stream()
                .map(WaifuMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaifuSkinUnlockResponse> findCurrentUserSkinUnlocks() {
        User user = currentUserService.getAuthenticatedUser();

        return waifuSkinUnlockRepository.findByUserEmailOrderByUnlockedAtDesc(user.getEmail())
                .stream()
                .map(WaifuMapper::toResponse)
                .toList();
    }

    @Transactional
    public WaifuSkinUnlockResponse unlockSkin(String waifuId, WaifuSkinUnlockRequest request) {
        User user = currentUserService.getAuthenticatedUser();
        Waifu waifu = waifuRepository.findById(waifuId)
                .orElseThrow(() -> new ResourceNotFoundException("Waifu not found"));

        if (waifuSkinUnlockRepository.existsByUserEmailAndWaifu_IdAndSkinCode(
                user.getEmail(),
                waifuId,
                request.skinCode()
        )) {
            throw new DuplicateResourceException("Waifu skin already unlocked");
        }

        LocalDateTime now = LocalDateTime.now();
        WaifuSkinUnlock unlock = WaifuSkinUnlock.builder()
                .user(user)
                .waifu(waifu)
                .skinCode(request.skinCode())
                .skinName(request.skinName())
                .unlockSource(request.unlockSource())
                .unlockedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return WaifuMapper.toResponse(waifuSkinUnlockRepository.save(unlock));
    }
}
