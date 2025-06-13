package com.example.economicssimulatorserver.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;


/**
 * Сущность refresh-токена пользователя для обновления сессии.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(columnList = "token", unique = true),
        @Index(columnList = "user_id")
})
public class RefreshToken {

    /**
     * Уникальный идентификатор токена.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Значение токена.
     */
    @Column(nullable = false, unique = true, length = 256)
    private String token;

    /**
     * Пользователь, которому принадлежит токен.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Дата истечения срока действия токена.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}