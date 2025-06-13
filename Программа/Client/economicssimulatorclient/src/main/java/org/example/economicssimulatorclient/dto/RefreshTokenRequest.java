package org.example.economicssimulatorclient.dto;

/**
 * Запрос на обновление токена доступа.
 *
 * @param refreshToken токен обновления
 */
public record RefreshTokenRequest(
        String refreshToken
) {}
