package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCreateRequestDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "n", "t", "v", "d", "desc", 1);
        ModelResultDto result = new ModelResultDto(2L, 2L, "t", "d", "c");
        ReportChartImageDto chart = new ReportChartImageDto("pie", "img");
        LlmChatResponseDto msg = new LlmChatResponseDto("msg");

        ReportCreateRequestDto dto = new ReportCreateRequestDto(
                100L, "IS-LM", "Отчёт", "ru",
                List.of(param), result, List.of(chart), List.of(msg), "text"
        );

        assertThat(dto.modelId()).isEqualTo(100L);
        assertThat(dto.modelName()).isEqualTo("IS-LM");
        assertThat(dto.name()).isEqualTo("Отчёт");
        assertThat(dto.language()).isEqualTo("ru");
        assertThat(dto.parameters()).containsExactly(param);
        assertThat(dto.result()).isEqualTo(result);
        assertThat(dto.charts()).containsExactly(chart);
        assertThat(dto.llmMessages()).containsExactly(msg);
        assertThat(dto.parsedResult()).isEqualTo("text");
    }

    @Test
    void equalsHashCodeToString() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "n", "t", "v", "d", "desc", 1);
        ModelResultDto result = new ModelResultDto(2L, 2L, "t", "d", "c");
        ReportChartImageDto chart = new ReportChartImageDto("pie", "img");
        LlmChatResponseDto msg = new LlmChatResponseDto("msg");

        ReportCreateRequestDto dto1 = new ReportCreateRequestDto(
                1L, "m", "n", "ru", List.of(param), result, List.of(chart), List.of(msg), "r"
        );
        ReportCreateRequestDto dto2 = new ReportCreateRequestDto(
                1L, "m", "n", "ru", List.of(param), result, List.of(chart), List.of(msg), "r"
        );

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("ReportCreateRequestDto");
    }
}
