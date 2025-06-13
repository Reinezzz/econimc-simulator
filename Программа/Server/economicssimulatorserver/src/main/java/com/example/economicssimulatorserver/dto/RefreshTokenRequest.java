package com.example.economicssimulatorserver.dto;

/**
 * Запрос на получение новой пары токенов.
 *
 * @param refreshToken действующий refresh токен
 */
public record RefreshTokenRequest(
        String refreshToken
) {}
