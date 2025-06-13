package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelParameterDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto dto = new ModelParameterDto(1L, 2L, "n", "t", "v", "d", "desc", 3);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.modelId()).isEqualTo(2L);
        assertThat(dto.paramName()).isEqualTo("n");
        assertThat(dto.paramType()).isEqualTo("t");
        assertThat(dto.paramValue()).isEqualTo("v");
        assertThat(dto.displayName()).isEqualTo("d");
        assertThat(dto.description()).isEqualTo("desc");
        assertThat(dto.customOrder()).isEqualTo(3);
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto dto1 = new ModelParameterDto(1L, 2L, "a", "b", "c", "d", "e", 1);
        ModelParameterDto dto2 = new ModelParameterDto(1L, 2L, "a", "b", "c", "d", "e", 1);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("ModelParameterDto");
    }
}
