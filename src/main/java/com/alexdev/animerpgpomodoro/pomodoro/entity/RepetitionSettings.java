package com.alexdev.animerpgpomodoro.pomodoro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repetition_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepetitionSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private Integer repeatCount;

    @Column(nullable = false)
    private boolean repeatDaily;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pomodoro_id", nullable = false, unique = true)
    private Pomodoro pomodoro;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
