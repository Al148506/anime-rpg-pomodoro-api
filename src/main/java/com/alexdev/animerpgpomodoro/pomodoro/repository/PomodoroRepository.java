package com.alexdev.animerpgpomodoro.pomodoro.repository;

import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PomodoroRepository extends JpaRepository<Pomodoro, String> {

    List<Pomodoro> findByUserEmail(String email);

    Optional<Pomodoro> findByIdAndUserEmail(String id, String email);
}
