package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportListItemDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDto dto = new ReportListItemDto(1L, "Отчёт", "IS-LM", "/files/report.pdf", now, "ru");

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Отчёт");
        assertThat(dto.modelName()).isEqualTo("IS-LM");
        assertThat(dto.path()).isEqualTo("/files/report.pdf");
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.language()).isEqualTo("ru");
    }

    @Test
    void equalsHashCodeToString() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDto dto1 = new ReportListItemDto(1L, "n", "m", "/f", now, "ru");
        ReportListItemDto dto2 = new ReportListItemDto(1L, "n", "m", "/f", now, "ru");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("ReportListItemDto");
    }
}
