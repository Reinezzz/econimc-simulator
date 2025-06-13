package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportChartImageDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        ReportChartImageDto dto = new ReportChartImageDto("pie", "base64str");

        assertThat(dto.chartType()).isEqualTo("pie");
        assertThat(dto.imageBase64()).isEqualTo("base64str");
    }

    @Test
    void equalsHashCodeToString() {
        ReportChartImageDto dto1 = new ReportChartImageDto("pie", "img");
        ReportChartImageDto dto2 = new ReportChartImageDto("pie", "img");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("ReportChartImageDto");
    }
}
