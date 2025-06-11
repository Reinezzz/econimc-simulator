package org.example.economicssimulatorclient.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
