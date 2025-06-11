package com.example.economicssimulatorserver.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
