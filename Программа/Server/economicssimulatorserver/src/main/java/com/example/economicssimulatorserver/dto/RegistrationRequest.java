package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на регистрацию нового пользователя.
 *
 * @param username имя пользователя
 * @param email    email пользователя (валидируется как email)
 * @param password пароль пользователя
 */
public record RegistrationRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) {}
