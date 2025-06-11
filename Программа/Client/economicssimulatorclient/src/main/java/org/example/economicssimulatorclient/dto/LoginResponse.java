package org.example.economicssimulatorclient.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
