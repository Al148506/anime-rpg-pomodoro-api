package com.alexdev.animerpgpomodoro.progression.repository;

import com.alexdev.animerpgpomodoro.progression.entity.PlayerProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerProgressRepository extends JpaRepository<PlayerProgress, String> {

    Optional<PlayerProgress> findByUserEmail(String email);
}
