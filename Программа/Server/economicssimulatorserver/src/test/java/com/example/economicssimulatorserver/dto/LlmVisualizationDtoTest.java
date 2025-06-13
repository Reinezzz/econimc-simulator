package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LlmVisualizationDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        Map<String, Object> chartData = Map.of("x", 1, "y", 2);
        LlmVisualizationDto dto = new LlmVisualizationDto("key", "title", chartData);

        assertThat(dto.chartKey()).isEqualTo("key");
        assertThat(dto.chartTitle()).isEqualTo("title");
        assertThat(dto.chartData()).isEqualTo(chartData);
    }

    @Test
    void equalsHashCodeToString() {
        Map<String, Object> chartData = Map.of("k", "v");
        LlmVisualizationDto dto1 = new LlmVisualizationDto("k", "t", chartData);
        LlmVisualizationDto dto2 = new LlmVisualizationDto("k", "t", chartData);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("LlmVisualizationDto");
    }
}
