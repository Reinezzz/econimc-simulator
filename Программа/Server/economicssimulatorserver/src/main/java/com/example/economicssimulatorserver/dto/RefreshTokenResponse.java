package com.example.economicssimulatorserver.dto;

/**
 * Ответ на запрос обновления токена.
 *
 * @param accessToken  новый токен доступа
 * @param refreshToken новый refresh токен
 */
public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
