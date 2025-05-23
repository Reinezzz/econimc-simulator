package com.example.economicssimulatorserver.dto;

import lombok.Data;

/**
 * DTO для запроса на обновление токена.
 */
public record TokenRefreshRequest (
    String refreshToken
) {}
