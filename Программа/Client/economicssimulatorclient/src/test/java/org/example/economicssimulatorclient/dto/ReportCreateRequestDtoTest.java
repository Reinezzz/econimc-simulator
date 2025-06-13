package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCreateRequestDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ModelParameterDto param = new ModelParameterDto(1L, 1L, "a", "double", "1", "A", "desc", 1);
        ModelResultDto result = new ModelResultDto(2L, 1L, "SUM", "42", "now");
        ReportChartImageDto chart = new ReportChartImageDto("line", "base64");
        LlmChatResponseDto llm = new LlmChatResponseDto("msg");

        ReportCreateRequestDto dto1 = new ReportCreateRequestDto(
                1L, "ModelName", "ReportName", "RU",
                List.of(param), result, List.of(chart), List.of(llm), "parsed"
        );
        ReportCreateRequestDto dto2 = new ReportCreateRequestDto(
                1L, "ModelName", "ReportName", "RU",
                List.of(param), result, List.of(chart), List.of(llm), "parsed"
        );

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.modelName()).isEqualTo("ModelName");
    }
}
