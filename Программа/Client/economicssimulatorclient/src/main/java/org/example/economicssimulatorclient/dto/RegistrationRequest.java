package org.example.economicssimulatorclient.dto;

/**
 * Запрос на регистрацию нового пользователя.
 *
 * @param username имя пользователя
 * @param email    адрес электронной почты
 * @param password пароль
 */
public record RegistrationRequest(
        String username,
        String email,
        String password
) {}
