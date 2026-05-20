package com.alexdev.animerpgpomodoro.waifu.dto;

import com.alexdev.animerpgpomodoro.waifu.entity.WaifuRarity;

import java.time.LocalDateTime;

public record WaifuResponse(
        String id,
        String name,
        String description,
        String defaultSkinCode,
        WaifuRarity rarity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
