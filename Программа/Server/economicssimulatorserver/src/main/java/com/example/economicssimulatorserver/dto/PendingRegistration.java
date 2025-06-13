package com.example.economicssimulatorserver.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Данные о регистрации, ожидающей подтверждения пользователем.
 * Хранятся временно до прохождения верификации.
 */
public class PendingRegistration implements Serializable {

    public String username;

    public String email;

    public String passwordHash;

    public String code;

    public Instant expiresAt;

    /**
     * Создаёт объект незавершённой регистрации.
     *
     * @param username     имя пользователя
     * @param email        электронная почта
     * @param passwordHash хеш пароля
     * @param code         код подтверждения
     * @param expiresAt    момент истечения срока действия кода
     */
    public PendingRegistration(String username, String email, String passwordHash, String code, Instant expiresAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.code = code;
        this.expiresAt = expiresAt;
    }
}
