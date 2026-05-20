package com.alexdev.animerpgpomodoro.waifu.repository;

import com.alexdev.animerpgpomodoro.waifu.entity.WaifuSkinUnlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaifuSkinUnlockRepository extends JpaRepository<WaifuSkinUnlock, String> {

    List<WaifuSkinUnlock> findByUserEmailOrderByUnlockedAtDesc(String email);

    boolean existsByUserEmailAndWaifu_IdAndSkinCode(String email, String waifuId, String skinCode);
}
