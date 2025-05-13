package com.example.economicssimulatorserver.dto;

public record PasswordResetConfirm(
        String email,
        String code,
        String newPassword
) {}
