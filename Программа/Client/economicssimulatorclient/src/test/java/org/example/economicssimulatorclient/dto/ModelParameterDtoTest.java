package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelParameterDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto dto1 = new ModelParameterDto(1L, 2L, "name", "double", "123", "display", "desc", 0);
        ModelParameterDto dto2 = new ModelParameterDto(1L, 2L, "name", "double", "123", "display", "desc", 0);

        assertThat(dto1.paramName()).isEqualTo("name");
        assertThat(dto1).isEqualTo(dto2);
    }
}
