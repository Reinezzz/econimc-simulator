package com.example.economicssimulatorserver.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
