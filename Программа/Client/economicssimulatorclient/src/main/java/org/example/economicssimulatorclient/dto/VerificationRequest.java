package org.example.economicssimulatorclient.dto;

public record VerificationRequest(
        String email,
        String code
) {}
