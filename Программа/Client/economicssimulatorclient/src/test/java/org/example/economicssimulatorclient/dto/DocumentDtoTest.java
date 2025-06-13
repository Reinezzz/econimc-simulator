package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        LocalDateTime uploadedAt = LocalDateTime.now();
        DocumentDto dto1 = new DocumentDto(1L, 10L, "test.pdf", "docs/test.pdf", uploadedAt);
        DocumentDto dto2 = new DocumentDto(1L, 10L, "test.pdf", "docs/test.pdf", uploadedAt);

        assertThat(dto1.id()).isEqualTo(1L);
        assertThat(dto1.userId()).isEqualTo(10L);
        assertThat(dto1.name()).isEqualTo("test.pdf");
        assertThat(dto1.path()).isEqualTo("docs/test.pdf");
        assertThat(dto1.uploadedAt()).isEqualTo(uploadedAt);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
