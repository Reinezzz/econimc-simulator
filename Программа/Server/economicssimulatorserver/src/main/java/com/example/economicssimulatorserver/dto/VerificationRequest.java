package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationRequest(
        @NotBlank String email,
        @NotBlank String code           // шестизначный
) {}
