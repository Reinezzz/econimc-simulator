package com.example.economicssimulatorserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для ответа на запрос обновления токена.
 */
public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
