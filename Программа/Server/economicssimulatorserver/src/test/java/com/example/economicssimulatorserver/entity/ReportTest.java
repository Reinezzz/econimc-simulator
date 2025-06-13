package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        OffsetDateTime now = OffsetDateTime.now();
        Report report = Report.builder()
                .id(1L)
                .userId(2L)
                .modelId(3L)
                .name("report")
                .modelName("IS-LM")
                .path("/files/report.pdf")
                .createdAt(now)
                .language("ru")
                .params("p")
                .result("res")
                .llmMessages("msg")
                .build();

        assertThat(report.getId()).isEqualTo(1L);
        assertThat(report.getUserId()).isEqualTo(2L);
        assertThat(report.getModelId()).isEqualTo(3L);
        assertThat(report.getName()).isEqualTo("report");
        assertThat(report.getModelName()).isEqualTo("IS-LM");
        assertThat(report.getPath()).isEqualTo("/files/report.pdf");
        assertThat(report.getCreatedAt()).isEqualTo(now);
        assertThat(report.getLanguage()).isEqualTo("ru");
        assertThat(report.getParams()).isEqualTo("p");
        assertThat(report.getResult()).isEqualTo("res");
        assertThat(report.getLlmMessages()).isEqualTo("msg");
    }

}
