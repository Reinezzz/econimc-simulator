package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LlmVisualizationDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        Map<String, Object> chartData = Map.of("val", 42);
        LlmVisualizationDto dto1 = new LlmVisualizationDto("key", "title", chartData);
        LlmVisualizationDto dto2 = new LlmVisualizationDto("key", "title", chartData);

        assertThat(dto1.chartKey()).isEqualTo("key");
        assertThat(dto1.chartTitle()).isEqualTo("title");
        assertThat(dto1.chartData()).isEqualTo(chartData);
        assertThat(dto1).isEqualTo(dto2);
    }
}
