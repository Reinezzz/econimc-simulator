package org.example.economicssimulatorclient.dto;

/**
 * DTO для запроса регистрации нового пользователя.
 *
 * @param username логин пользователя
 * @param email email пользователя
 * @param password пароль пользователя
 */
public record RegistrationRequest(
        String username,
        String email,
        String password
) {}
