package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmParameterExtractionRequestDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        LlmParameterExtractionRequestDto dto = new LlmParameterExtractionRequestDto(12L, 34L);
        assertThat(dto.modelId()).isEqualTo(12L);
        assertThat(dto.documentId()).isEqualTo(34L);
    }

    @Test
    void equalsHashCodeToString() {
        LlmParameterExtractionRequestDto dto1 = new LlmParameterExtractionRequestDto(1L, 2L);
        LlmParameterExtractionRequestDto dto2 = new LlmParameterExtractionRequestDto(1L, 2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("LlmParameterExtractionRequestDto");
    }
}
