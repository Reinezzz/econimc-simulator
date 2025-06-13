package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelResultDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelResultDto dto = new ModelResultDto(1L, 2L, "graph", "data", "2024-06-13T10:00");

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.modelId()).isEqualTo(2L);
        assertThat(dto.resultType()).isEqualTo("graph");
        assertThat(dto.resultData()).isEqualTo("data");
        assertThat(dto.calculatedAt()).isEqualTo("2024-06-13T10:00");
    }

    @Test
    void equalsHashCodeToString() {
        ModelResultDto dto1 = new ModelResultDto(1L, 2L, "t", "d", "c");
        ModelResultDto dto2 = new ModelResultDto(1L, 2L, "t", "d", "c");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("ModelResultDto");
    }
}
