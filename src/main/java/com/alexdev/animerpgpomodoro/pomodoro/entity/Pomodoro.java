package com.alexdev.animerpgpomodoro.pomodoro.entity;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pomodoros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pomodoro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PomodoroType type;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer shortBreakMinutes;

    @Column(nullable = false)
    private Integer longBreakMinutes;

    @Column(nullable = false)
    private Integer sessionsBeforeLongBreak;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "pomodoro", cascade = CascadeType.ALL, orphanRemoval = true)
    private RepetitionSettings repetitionSettings;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
