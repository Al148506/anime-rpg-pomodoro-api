package com.alexdev.animerpgpomodoro.session.entity;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.pomodoro.entity.Pomodoro;
import com.alexdev.animerpgpomodoro.pomodoro.entity.PomodoroType;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pomodoro_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PomodoroSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pomodoro_id")
    private Pomodoro pomodoro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PomodoroType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PomodoroSessionStatus status;

    @Column(nullable = false)
    private Integer plannedDurationMinutes;

    private Integer actualDurationMinutes;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
