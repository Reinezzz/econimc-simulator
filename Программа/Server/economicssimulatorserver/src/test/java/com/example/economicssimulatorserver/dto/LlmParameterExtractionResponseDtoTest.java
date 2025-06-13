package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmParameterExtractionResponseDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto param = new ModelParameterDto(1L, 2L, "name", "type", "val", "disp", "desc", 1);
        LlmParameterExtractionResponseDto dto = new LlmParameterExtractionResponseDto(List.of(param));
        assertThat(dto.parameters()).containsExactly(param);
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto param = new ModelParameterDto(1L, 2L, "name", "type", "val", "disp", "desc", 1);
        LlmParameterExtractionResponseDto dto1 = new LlmParameterExtractionResponseDto(List.of(param));
        LlmParameterExtractionResponseDto dto2 = new LlmParameterExtractionResponseDto(List.of(param));

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("LlmParameterExtractionResponseDto");
    }
}
