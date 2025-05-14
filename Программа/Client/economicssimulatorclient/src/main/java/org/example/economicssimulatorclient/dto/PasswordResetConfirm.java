package org.example.economicssimulatorclient.dto;

public record PasswordResetConfirm(
        String email,
        String code,
        String newPassword
) {}
