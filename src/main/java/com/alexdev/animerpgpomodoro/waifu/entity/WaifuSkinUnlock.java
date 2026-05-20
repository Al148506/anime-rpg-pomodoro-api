package com.alexdev.animerpgpomodoro.waifu.entity;

import com.alexdev.animerpgpomodoro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "waifu_skin_unlocks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_waifu_skin_unlock_user_waifu_skin",
                columnNames = {"user_id", "waifu_id", "skin_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaifuSkinUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waifu_id", nullable = false)
    private Waifu waifu;

    @Column(name = "skin_code", nullable = false, length = 80)
    private String skinCode;

    @Column(nullable = false, length = 120)
    private String skinName;

    @Column(nullable = false, length = 120)
    private String unlockSource;

    @Column(nullable = false)
    private LocalDateTime unlockedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
