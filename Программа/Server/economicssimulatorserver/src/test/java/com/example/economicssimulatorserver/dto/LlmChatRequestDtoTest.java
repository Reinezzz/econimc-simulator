package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmChatRequestDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto param = new ModelParameterDto(1L, 2L, "pn", "pt", "pv", "dn", "desc", 1);
        LlmVisualizationDto vis = new LlmVisualizationDto("key", "title", java.util.Map.of("k", 1));
        ModelResultDto result = new ModelResultDto(1L, 2L, "type", "data", "2024-06-01T10:00");
        LlmChatRequestDto dto = new LlmChatRequestDto(7L, "msg", List.of(param), List.of(vis), result);

        assertThat(dto.modelId()).isEqualTo(7L);
        assertThat(dto.userMessage()).isEqualTo("msg");
        assertThat(dto.parameters()).containsExactly(param);
        assertThat(dto.visualizations()).containsExactly(vis);
        assertThat(dto.result()).isEqualTo(result);
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "p", "t", "v", "d", "desc", 1);
        LlmVisualizationDto vis = new LlmVisualizationDto("k", "t", java.util.Map.of());
        ModelResultDto result = new ModelResultDto(1L, 1L, "t", "d", "c");
        LlmChatRequestDto dto1 = new LlmChatRequestDto(2L, "u", List.of(param), List.of(vis), result);
        LlmChatRequestDto dto2 = new LlmChatRequestDto(2L, "u", List.of(param), List.of(vis), result);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("LlmChatRequestDto");
    }
}
