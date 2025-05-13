package com.example.economicssimulatorserver.dto;

public record RegistrationRequest(
        String username,
        String email,
        String password
) {}
