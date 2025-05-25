package com.example.economicssimulatorserver.dto;

/**
 * Response DTO containing new access and refresh tokens.
 * @param accessToken new JWT access token
 * @param refreshToken new refresh token
 */
public record RefreshTokenResponse(String accessToken, String refreshToken) {}
