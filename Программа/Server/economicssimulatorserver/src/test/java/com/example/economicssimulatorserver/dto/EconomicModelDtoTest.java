package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EconomicModelDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto param = new ModelParameterDto(1L, 2L, "p", "t", "v", "dn", "desc", 1);
        ModelResultDto result = new ModelResultDto(11L, 2L, "type", "res", "2024-06-01T12:00");
        EconomicModelDto dto = new EconomicModelDto(1L, "macro", "name", "desc", List.of(param), List.of(result), "2024-06-01T12:00", "2024-06-02T12:00", "x+y=z");

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.modelType()).isEqualTo("macro");
        assertThat(dto.name()).isEqualTo("name");
        assertThat(dto.description()).isEqualTo("desc");
        assertThat(dto.parameters()).containsExactly(param);
        assertThat(dto.results()).containsExactly(result);
        assertThat(dto.createdAt()).isEqualTo("2024-06-01T12:00");
        assertThat(dto.updatedAt()).isEqualTo("2024-06-02T12:00");
        assertThat(dto.formula()).isEqualTo("x+y=z");
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "n", "t", "v", "d", "desc", 1);
        ModelResultDto result = new ModelResultDto(1L, 1L, "t", "d", "c");
        EconomicModelDto dto1 = new EconomicModelDto(1L, "t", "n", "d", List.of(param), List.of(result), "c", "u", "f");
        EconomicModelDto dto2 = new EconomicModelDto(1L, "t", "n", "d", List.of(param), List.of(result), "c", "u", "f");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("EconomicModelDto");
    }
}
