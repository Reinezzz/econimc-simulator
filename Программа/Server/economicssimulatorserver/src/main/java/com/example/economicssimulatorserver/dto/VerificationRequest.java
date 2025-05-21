package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос для подтверждения email пользователя при регистрации.
 *
 * @param email email пользователя
 * @param code  шестизначный одноразовый код подтверждения
 */
public record VerificationRequest(
        @NotBlank String email,
        @NotBlank String code
) {}
