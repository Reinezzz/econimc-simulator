package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelResultDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelResultDto dto1 = new ModelResultDto(1L, 2L, "type", "data", "now");
        ModelResultDto dto2 = new ModelResultDto(1L, 2L, "type", "data", "now");

        assertThat(dto1.resultType()).isEqualTo("type");
        assertThat(dto1).isEqualTo(dto2);
    }
}
