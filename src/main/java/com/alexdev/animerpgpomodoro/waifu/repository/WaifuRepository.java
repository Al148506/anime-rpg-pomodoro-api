package com.alexdev.animerpgpomodoro.waifu.repository;

import com.alexdev.animerpgpomodoro.waifu.entity.Waifu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaifuRepository extends JpaRepository<Waifu, String> {

    boolean existsByName(String name);
}
