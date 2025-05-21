package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос для аутентификации пользователя по логину/email и паролю.
 *
 * @param usernameOrEmail логин или email пользователя
 * @param password        пароль пользователя
 */
public record LoginRequest(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) {}
