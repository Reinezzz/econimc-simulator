package org.example.economicssimulatorclient.dto;

public record RegistrationRequest(
        String username,
        String email,
        String password
) {}
