package org.example.economicssimulatorclient.dto;

/**
 * Универсальный DTO для ответа от API.
 *
 * @param success успешность выполнения запроса
 * @param message текстовое сообщение или ключ локализации
 */
public record ApiResponse(
        boolean success,
        String message
) {}
