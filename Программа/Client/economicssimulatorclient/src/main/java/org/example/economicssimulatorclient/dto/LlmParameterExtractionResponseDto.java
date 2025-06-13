package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Результат извлечения параметров из документа.
 *
 * @param parameters найденные параметры модели
 */
public record LlmParameterExtractionResponseDto(
        List<ModelParameterDto> parameters
) {}
