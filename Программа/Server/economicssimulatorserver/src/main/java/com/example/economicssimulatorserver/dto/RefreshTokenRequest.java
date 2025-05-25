package com.example.economicssimulatorserver.dto;

/**
 * Request DTO for refreshing tokens.
 * @param refreshToken the refresh token value
 */
public record RefreshTokenRequest(String refreshToken) {}
