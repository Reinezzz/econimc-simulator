package com.example.economicssimulatorserver.dto;

/**
 * Запрос на выход из системы (logout), содержит refresh token.
 * @param refreshToken refresh токен пользователя
 */
public record LogoutRequest(String refreshToken) {}
