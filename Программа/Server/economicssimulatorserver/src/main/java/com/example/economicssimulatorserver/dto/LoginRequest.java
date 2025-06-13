package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на аутентификацию пользователя.
 *
 * @param usernameOrEmail имя пользователя или его электронная почта
 * @param password        пароль в открытом виде
 */
public record LoginRequest(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) {}
