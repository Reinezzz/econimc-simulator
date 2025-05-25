package org.example.economicssimulatorclient.dto;

/**
 * DTO-ответ с новыми access и refresh токенами.
 * @param accessToken новый access токен
 * @param refreshToken новый refresh токен
 */
public record RefreshTokenResponse(String accessToken, String refreshToken) {}
