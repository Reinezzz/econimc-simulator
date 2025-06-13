package org.example.economicssimulatorclient.dto;

/**
 * Ответ сервера при обновлении токена доступа.
 *
 * @param accessToken  новый токен доступа
 * @param refreshToken новый токен обновления
 */
public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
