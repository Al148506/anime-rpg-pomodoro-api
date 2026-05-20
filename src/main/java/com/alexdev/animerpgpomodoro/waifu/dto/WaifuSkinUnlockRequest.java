package com.alexdev.animerpgpomodoro.waifu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WaifuSkinUnlockRequest(
        @NotBlank(message = "Skin code is required")
        @Size(max = 80)
        String skinCode,

        @NotBlank(message = "Skin name is required")
        @Size(max = 120)
        String skinName,

        @NotBlank(message = "Unlock source is required")
        @Size(max = 120)
        String unlockSource
) {
}
