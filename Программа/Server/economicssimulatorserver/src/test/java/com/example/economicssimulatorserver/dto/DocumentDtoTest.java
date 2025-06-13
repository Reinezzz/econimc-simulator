package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        LocalDateTime now = LocalDateTime.now();
        DocumentDto dto = new DocumentDto(123L, 456L, "name.pdf", "/files/name.pdf", now);

        assertThat(dto.id()).isEqualTo(123L);
        assertThat(dto.userId()).isEqualTo(456L);
        assertThat(dto.name()).isEqualTo("name.pdf");
        assertThat(dto.path()).isEqualTo("/files/name.pdf");
        assertThat(dto.uploadedAt()).isEqualTo(now);
    }

    @Test
    void equalsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();
        DocumentDto dto1 = new DocumentDto(1L, 2L, "f", "/p", now);
        DocumentDto dto2 = new DocumentDto(1L, 2L, "f", "/p", now);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("DocumentDto");
    }
}
