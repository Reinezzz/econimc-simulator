package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationRequestDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "p", "t", "v", "d", "desc", 1);
        CalculationRequestDto dto = new CalculationRequestDto(100L, "macro", List.of(param));

        assertThat(dto.modelId()).isEqualTo(100L);
        assertThat(dto.modelType()).isEqualTo("macro");
        assertThat(dto.parameters()).containsExactly(param);
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto param1 = new ModelParameterDto(1L, 1L, "p", "t", "v", "d", "desc", 1);
        ModelParameterDto param2 = new ModelParameterDto(1L, 1L, "p", "t", "v", "d", "desc", 1);
        CalculationRequestDto dto1 = new CalculationRequestDto(10L, "macro", List.of(param1));
        CalculationRequestDto dto2 = new CalculationRequestDto(10L, "macro", List.of(param2));

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("CalculationRequestDto");
    }
}
