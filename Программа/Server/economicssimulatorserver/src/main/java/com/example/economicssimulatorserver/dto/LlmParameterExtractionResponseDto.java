package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Результат извлечения параметров модели из документа.
 *
 * @param parameters найденные и распознанные параметры модели
 */
public record LlmParameterExtractionResponseDto(
        List<ModelParameterDto> parameters
) {}