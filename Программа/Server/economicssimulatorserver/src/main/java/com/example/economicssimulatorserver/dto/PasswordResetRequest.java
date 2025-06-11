package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
        @NotBlank String email
) {}
