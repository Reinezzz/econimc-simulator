package org.example.economicssimulatorclient.dto;

/**
 * Запрос на отправку письма для сброса пароля.
 *
 * @param email адрес электронной почты пользователя
 */
public record PasswordResetRequest(
        String email
) {}
