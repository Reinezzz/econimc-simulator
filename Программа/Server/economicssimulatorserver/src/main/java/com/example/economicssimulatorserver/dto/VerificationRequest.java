package com.example.economicssimulatorserver.dto;

public record VerificationRequest(
        String email,
        String code           // шестизначный
) {}
