package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportChartImageDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        ReportChartImageDto dto1 = new ReportChartImageDto("line", "base64string");
        ReportChartImageDto dto2 = new ReportChartImageDto("line", "base64string");

        assertThat(dto1.chartType()).isEqualTo("line");
        assertThat(dto1.imageBase64()).isEqualTo("base64string");
        assertThat(dto1).isEqualTo(dto2);
    }
}
