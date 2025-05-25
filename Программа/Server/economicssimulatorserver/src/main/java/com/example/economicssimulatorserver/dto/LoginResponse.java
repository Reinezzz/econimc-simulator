package com.example.economicssimulatorserver.dto;

/**
 * DTO для ответа при логине. Содержит access и refresh токены и их тип.
 * @param accessToken access токен (JWT)
 * @param refreshToken refresh токен
 * @param tokenType тип токена, например "Bearer"
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
