package org.example.economicssimulatorclient.dto;

/**
 * DTO для инициации процедуры сброса пароля.
 *
 * @param email email пользователя
 */
public record PasswordResetRequest(
        String email
) {}
