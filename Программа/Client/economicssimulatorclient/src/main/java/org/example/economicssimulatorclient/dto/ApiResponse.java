package org.example.economicssimulatorclient.dto;

/**
 * Ответ API с признаком успешности.
 * Используется для передачи результата выполнения запросов сервера.
 *
 * @param success флаг удачного завершения операции
 * @param message поясняющее сообщение
 */
public record ApiResponse(
        boolean success,
        String message
) {}
