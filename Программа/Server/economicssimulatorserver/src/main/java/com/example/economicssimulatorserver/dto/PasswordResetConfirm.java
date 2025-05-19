package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirm(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank String newPassword
) {}
