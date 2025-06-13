package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Токен для восстановления пароля пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /**
     * Уникальный идентификатор токена.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, запросивший восстановление пароля.
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