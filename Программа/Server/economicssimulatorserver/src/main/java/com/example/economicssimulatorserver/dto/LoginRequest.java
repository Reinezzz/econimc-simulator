package com.example.economicssimulatorserver.dto;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
