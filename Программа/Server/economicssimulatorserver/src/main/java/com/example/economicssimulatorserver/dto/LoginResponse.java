package com.example.economicssimulatorserver.dto;

/**
 * Ответ на успешную аутентификацию.
 *
 * @param accessToken  JWT токен доступа
 * @param refreshToken токен обновления сессии
 * @param tokenType    тип выдаваемого токена
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
