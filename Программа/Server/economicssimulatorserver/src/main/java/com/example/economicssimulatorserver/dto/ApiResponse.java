package com.example.economicssimulatorserver.dto;

/**
 * Унифицированный ответ для REST-эндпоинтов API.
 *
 * @param success  успешность выполнения операции
 * @param message  текстовое сообщение или ключ локализации
 */
public record ApiResponse(
        boolean success,
        String message
) {}
