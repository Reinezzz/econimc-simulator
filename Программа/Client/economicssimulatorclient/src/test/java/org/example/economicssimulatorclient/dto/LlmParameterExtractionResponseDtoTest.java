package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmParameterExtractionResponseDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "p", "double", "1", "P", "desc", 1);
        LlmParameterExtractionResponseDto dto1 = new LlmParameterExtractionResponseDto(List.of(param));
        LlmParameterExtractionResponseDto dto2 = new LlmParameterExtractionResponseDto(List.of(param));

        assertThat(dto1.parameters()).containsExactly(param);
        assertThat(dto1).isEqualTo(dto2);
    }
}
