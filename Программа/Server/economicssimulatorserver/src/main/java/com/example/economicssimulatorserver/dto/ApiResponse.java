package com.example.economicssimulatorserver.dto;

/**
 * Обёртка ответа сервера на любой запрос.
 * <p>
 * Позволяет передать клиенту информацию об успешности операции
 * и сопроводительное сообщение.
 *
 * @param success флаг, показывающий успешность выполнения запроса
 * @param message текстовое сообщение о результате
 */
public record ApiResponse(
        boolean success,
        String message
) {}
