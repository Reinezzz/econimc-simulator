package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EconomicModelDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "a", "double", "1.1", "A", "desc", 1);
        ModelResultDto result = new ModelResultDto(2L, 1L, "SUM", "42", "now");
        EconomicModelDto dto1 = new EconomicModelDto(3L, "test", "TestModel", "desc",
                List.of(param), List.of(result), "2024-06-13T12:00:00", "2024-06-13T13:00:00", "f(x)=x");

        EconomicModelDto dto2 = new EconomicModelDto(3L, "test", "TestModel", "desc",
                List.of(param), List.of(result), "2024-06-13T12:00:00", "2024-06-13T13:00:00", "f(x)=x");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.id()).isEqualTo(3L);
        assertThat(dto1.modelType()).isEqualTo("test");
        assertThat(dto1.parameters()).containsExactly(param);
        assertThat(dto1.results()).containsExactly(result);
    }
}
