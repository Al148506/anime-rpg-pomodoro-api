package com.alexdev.animerpgpomodoro.waifu.mapper;

import com.alexdev.animerpgpomodoro.waifu.dto.WaifuResponse;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockResponse;
import com.alexdev.animerpgpomodoro.waifu.entity.Waifu;
import com.alexdev.animerpgpomodoro.waifu.entity.WaifuSkinUnlock;

public class WaifuMapper {

    private WaifuMapper() {
    }

    public static WaifuResponse toResponse(Waifu waifu) {
        return new WaifuResponse(
                waifu.getId(),
                waifu.getName(),
                waifu.getDescription(),
                waifu.getDefaultSkinCode(),
                waifu.getRarity(),
                waifu.getCreatedAt(),
                waifu.getUpdatedAt()
        );
    }

    public static WaifuSkinUnlockResponse toResponse(WaifuSkinUnlock unlock) {
        return new WaifuSkinUnlockResponse(
                unlock.getId(),
                unlock.getWaifu().getId(),
                unlock.getWaifu().getName(),
                unlock.getSkinCode(),
                unlock.getSkinName(),
                unlock.getUnlockSource(),
                unlock.getUnlockedAt(),
                unlock.getCreatedAt(),
                unlock.getUpdatedAt()
        );
    }
}
