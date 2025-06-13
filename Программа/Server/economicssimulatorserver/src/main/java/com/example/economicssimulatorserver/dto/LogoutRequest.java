package com.example.economicssimulatorserver.dto;

/**
 * Запрос на завершение сессии пользователя.
 *
 * @param refreshToken токен, подлежащий аннулированию
 */
public record LogoutRequest(
        String refreshToken
) {}
