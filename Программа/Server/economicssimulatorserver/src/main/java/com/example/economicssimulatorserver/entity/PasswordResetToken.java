package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность для хранения одноразовых токенов сброса пароля пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /** Уникальный идентификатор токена. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Пользователь, для которого создан токен. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** Одноразовый код сброса пароля (обычно 6 символов). */
    @Column(nullable = false, length = 6)
    private String code;

    /** Дата и время истечения срока действия токена. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
