package com.alexdev.animerpgpomodoro.session.repository;

import com.alexdev.animerpgpomodoro.session.entity.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, String> {

    List<PomodoroSession> findByUserEmailOrderByStartedAtDesc(String email);

    Optional<PomodoroSession> findByIdAndUserEmail(String id, String email);
}
