package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmParameterExtractionRequestDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        LlmParameterExtractionRequestDto dto1 = new LlmParameterExtractionRequestDto(1L, 2L);
        LlmParameterExtractionRequestDto dto2 = new LlmParameterExtractionRequestDto(1L, 2L);

        assertThat(dto1.modelId()).isEqualTo(1L);
        assertThat(dto1.documentId()).isEqualTo(2L);
        assertThat(dto1).isEqualTo(dto2);
    }
}
