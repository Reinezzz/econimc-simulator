package com.example.economicssimulatorserver.dto;

import java.util.List;

public record LlmParameterExtractionResponseDto(
        List<ModelParameterDto> parameters  // Список тех же параметров, только уже с новыми значениями paramValue
) {}