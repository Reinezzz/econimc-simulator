package org.example.economicssimulatorclient.dto;

/**
 * Ответ на успешную авторизацию.
 * Содержит пару токенов для работы с API.
 *
 * @param accessToken  токен доступа
 * @param refreshToken токен обновления
 * @param tokenType    тип токена
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
