package com.alexdev.animerpgpomodoro.waifu.dto;

import com.alexdev.animerpgpomodoro.waifu.entity.WaifuRarity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WaifuRequest(
        @NotBlank(message = "Waifu name is required")
        @Size(max = 120)
        String name,

        @Size(max = 1000)
        String description,

        @NotBlank(message = "Default skin code is required")
        @Size(max = 80)
        String defaultSkinCode,

        @NotNull(message = "Rarity is required")
        WaifuRarity rarity
) {
}
