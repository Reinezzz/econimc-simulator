package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmChatRequestDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "a", "double", "1.1", "A", "desc", 1);
        LlmVisualizationDto vis = new LlmVisualizationDto("key", "title", null);
        ModelResultDto result = new ModelResultDto(2L, 1L, "SUM", "42", "now");

        LlmChatRequestDto dto1 = new LlmChatRequestDto(1L, "msg", List.of(param), List.of(vis), result);
        LlmChatRequestDto dto2 = new LlmChatRequestDto(1L, "msg", List.of(param), List.of(vis), result);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.modelId()).isEqualTo(1L);
        assertThat(dto1.userMessage()).isEqualTo("msg");
    }
}
