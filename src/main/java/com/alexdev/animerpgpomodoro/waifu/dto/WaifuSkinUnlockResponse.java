package com.alexdev.animerpgpomodoro.waifu.dto;

import java.time.LocalDateTime;

public record WaifuSkinUnlockResponse(
        String id,
        String waifuId,
        String waifuName,
        String skinCode,
        String skinName,
        String unlockSource,
        LocalDateTime unlockedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
