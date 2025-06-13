package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportListItemDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDto dto1 = new ReportListItemDto(1L, "name", "model", "path", now, "RU");
        ReportListItemDto dto2 = new ReportListItemDto(1L, "name", "model", "path", now, "RU");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.name()).isEqualTo("name");
        assertThat(dto1.createdAt()).isEqualTo(now);
    }
}
