package org.example.economicssimulatorclient.dto;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
