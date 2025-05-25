package org.example.economicssimulatorclient.dto;

/**
 * DTO для ответа на успешный вход пользователя.
 *
 * @param accessToken access-токен (JWT) для последующих запросов
 * @param tokenType тип токена (обычно "Bearer")
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
