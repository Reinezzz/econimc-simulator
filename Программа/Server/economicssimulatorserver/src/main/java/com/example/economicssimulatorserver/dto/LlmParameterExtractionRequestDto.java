package com.example.economicssimulatorserver.dto;

/**
 * Запрос на извлечение параметров модели из документа с помощью ИИ.
 *
 * @param modelId    идентификатор экономической модели
 * @param documentId идентификатор документа, из которого извлекаются данные
 */
public record LlmParameterExtractionRequestDto(
        Long modelId,
        Long documentId
) {}
