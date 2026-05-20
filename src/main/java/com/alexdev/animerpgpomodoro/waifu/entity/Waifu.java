package com.alexdev.animerpgpomodoro.waifu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waifus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waifu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 80)
    private String defaultSkinCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WaifuRarity rarity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
