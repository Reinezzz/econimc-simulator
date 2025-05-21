package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Сущность пользователя системы.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    /** Уникальный идентификатор пользователя. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальное имя пользователя. */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Уникальный email пользователя. */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Хэш пароля пользователя. */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** true, если email подтвержден. */
    @Builder.Default
    private boolean enabled = false;

    /** Дата создания пользователя. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Дата последнего обновления пользователя. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
