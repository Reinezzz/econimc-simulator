package org.example.economicssimulatorclient.dto;

/**
 * DTO для подтверждения email или регистрации пользователя.
 *
 * @param email email пользователя
 * @param code одноразовый код подтверждения
 */
public record VerificationRequest(
        String email,
        String code
) {}
