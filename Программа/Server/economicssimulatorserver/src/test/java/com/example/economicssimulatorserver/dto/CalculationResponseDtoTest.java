package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationResponseDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelResultDto result = new ModelResultDto(1L, 1L, "type", "data", "2024-06-01T10:00");
        ModelParameterDto param = new ModelParameterDto(2L, 1L, "p", "t", "v", "d", "desc", 2);
        CalculationResponseDto dto = new CalculationResponseDto(result, List.of(param));

        assertThat(dto.result()).isEqualTo(result);
        assertThat(dto.updatedParameters()).containsExactly(param);
    }

    @Test
    void equalsHashCodeToString() {
        ModelResultDto r1 = new ModelResultDto(1L, 1L, "t", "d", "c");
        ModelResultDto r2 = new ModelResultDto(1L, 1L, "t", "d", "c");
        ModelParameterDto p1 = new ModelParameterDto(2L, 1L, "a", "b", "c", "d", "e", 2);
        ModelParameterDto p2 = new ModelParameterDto(2L, 1L, "a", "b", "c", "d", "e", 2);
        CalculationResponseDto dto1 = new CalculationResponseDto(r1, List.of(p1));
        CalculationResponseDto dto2 = new CalculationResponseDto(r2, List.of(p2));

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("CalculationResponseDto");
    }
}
