package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationRequestDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "a", "double", "42", "A", "desc", 1);
        CalculationRequestDto dto1 = new CalculationRequestDto(1L, "model", List.of(param));
        CalculationRequestDto dto2 = new CalculationRequestDto(1L, "model", List.of(param));

        assertThat(dto1.modelId()).isEqualTo(1L);
        assertThat(dto1.modelType()).isEqualTo("model");
        assertThat(dto1.parameters()).containsExactly(param);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("modelId=1", "modelType=model");
    }
}
