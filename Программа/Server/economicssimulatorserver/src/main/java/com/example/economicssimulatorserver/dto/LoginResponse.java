package com.example.economicssimulatorserver.dto;

public record LoginResponse(
        String accessToken,
        String tokenType      // обычно "Bearer"
) {}
