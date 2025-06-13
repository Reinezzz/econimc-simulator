package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на создание новой учётной записи.
 *
 * @param username имя пользователя
 * @param email    адрес электронной почты
 * @param password пароль пользователя
 */
public record RegistrationRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) {}
