package com.alexdev.animerpgpomodoro.waifu.controller;

import com.alexdev.animerpgpomodoro.waifu.dto.WaifuRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuResponse;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockRequest;
import com.alexdev.animerpgpomodoro.waifu.dto.WaifuSkinUnlockResponse;
import com.alexdev.animerpgpomodoro.waifu.service.WaifuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waifus")
public class WaifuController {

    private final WaifuService waifuService;
    public WaifuController(WaifuService waifuService) {
        this.waifuService = waifuService;
    }

    @GetMapping
    public ResponseEntity<List<WaifuResponse>> findAll() {
        return ResponseEntity.ok(waifuService.findAll());
    }

    @PostMapping
    public ResponseEntity<WaifuResponse> create(@Valid @RequestBody WaifuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waifuService.create(request));
    }

    @GetMapping("/skins/unlocks")
    public ResponseEntity<List<WaifuSkinUnlockResponse>> findCurrentUserSkinUnlocks() {
        return ResponseEntity.ok(waifuService.findCurrentUserSkinUnlocks());
    }

    @PostMapping("/{waifuId}/skins/unlock")
    public ResponseEntity<WaifuSkinUnlockResponse> unlockSkin(
            @PathVariable String waifuId,
            @Valid @RequestBody WaifuSkinUnlockRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waifuService.unlockSkin(waifuId, request));
    }
}
