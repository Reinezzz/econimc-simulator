package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на подтверждение регистрации пользователя.
 *
 * @param email электронная почта пользователя
 * @param code  код подтверждения
 */
public record VerificationRequest(
        @NotBlank String email,
        @NotBlank String code
) {}
