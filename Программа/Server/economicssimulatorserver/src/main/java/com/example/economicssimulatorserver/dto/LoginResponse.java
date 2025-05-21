package com.example.economicssimulatorserver.dto;

/**
 * Ответ, возвращаемый после успешной аутентификации пользователя.
 *
 * @param accessToken JWT access-токен для последующих запросов
 * @param tokenType   тип токена (обычно "Bearer")
 */
public record LoginResponse(
        String accessToken,
        String tokenType
) {}
