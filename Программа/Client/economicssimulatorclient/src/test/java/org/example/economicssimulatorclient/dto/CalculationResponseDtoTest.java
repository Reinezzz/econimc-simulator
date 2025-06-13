package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationResponseDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelResultDto result = new ModelResultDto(2L, 1L, "SUM", "42", "2024-06-13T12:00:00");
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "a", "double", "42", "A", "desc", 1);
        CalculationResponseDto dto1 = new CalculationResponseDto(result, List.of(param));
        CalculationResponseDto dto2 = new CalculationResponseDto(result, List.of(param));

        assertThat(dto1.result()).isEqualTo(result);
        assertThat(dto1.updatedParameters()).containsExactly(param);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("result=", "updatedParameters=");
    }
}
