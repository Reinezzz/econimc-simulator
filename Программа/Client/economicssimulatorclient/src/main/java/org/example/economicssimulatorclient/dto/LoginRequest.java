package org.example.economicssimulatorclient.dto;

/**
 * DTO для запроса аутентификации пользователя.
 *
 * @param usernameOrEmail логин или email пользователя
 * @param password пароль пользователя
 */
public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
