package com.example.economicssimulatorserver.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Вспомогательный объект для хранения данных незавершённой регистрации.
 * Используется для временного хранения (например, в кэше) до подтверждения email.
 */
public class PendingRegistration implements Serializable {
    /** Имя пользователя. */
    public String username;
    /** Email пользователя. */
    public String email;
    /** Хеш пароля пользователя. */
    public String passwordHash;
    /** Одноразовый код подтверждения. */
    public String code;
    /** Время истечения сессии регистрации. */
    public Instant expiresAt;

    /**
     * Конструктор полного набора полей PendingRegistration.
     *
     * @param username     имя пользователя
     * @param email        email пользователя
     * @param passwordHash хеш пароля
     * @param code         код подтверждения
     * @param expiresAt    срок действия сессии
     */
    public PendingRegistration(String username, String email, String passwordHash, String code, Instant expiresAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.code = code;
        this.expiresAt = expiresAt;
    }
}
