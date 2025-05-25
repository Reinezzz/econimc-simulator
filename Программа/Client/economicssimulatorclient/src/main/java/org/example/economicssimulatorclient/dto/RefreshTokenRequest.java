package org.example.economicssimulatorclient.dto;

/**
 * DTO-запрос для обновления токенов по refreshToken.
 * @param refreshToken refresh токен пользователя
 */
public record RefreshTokenRequest(String refreshToken) {}
