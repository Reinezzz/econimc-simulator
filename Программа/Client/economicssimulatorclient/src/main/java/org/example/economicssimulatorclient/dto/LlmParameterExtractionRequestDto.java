package org.example.economicssimulatorclient.dto;

/**
 * Запрос к ИИ на извлечение параметров из документа.
 *
 * @param modelId    идентификатор модели
 * @param documentId идентификатор документа
 */
public record LlmParameterExtractionRequestDto(
        Long modelId,
        Long documentId
) {}
