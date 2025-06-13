package org.example.economicssimulatorclient.dto;

/**
 * Запрос на авторизацию пользователя.
 *
 * @param usernameOrEmail имя пользователя или e-mail
 * @param password        пароль
 */
public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
