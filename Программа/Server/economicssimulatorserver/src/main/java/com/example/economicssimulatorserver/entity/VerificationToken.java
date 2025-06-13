package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Токен подтверждения регистрации пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    /**
     * Уникальный идентификатор токена.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, для которого создан токен.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Код подтверждения.
     */
    @Column(nullable = false, length = 6)
    private String code;

    /**
     * Время истечения срока действия токена.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}