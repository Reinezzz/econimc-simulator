package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentEntityTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        LocalDateTime time = LocalDateTime.now();
        DocumentEntity entity = DocumentEntity.builder()
                .id(1L)
                .userId(2L)
                .name("file.pdf")
                .path("/files/file.pdf")
                .uploadedAt(time)
                .build();

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getName()).isEqualTo("file.pdf");
        assertThat(entity.getPath()).isEqualTo("/files/file.pdf");
        assertThat(entity.getUploadedAt()).isEqualTo(time);
    }

    @Test
    void equalsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();
        DocumentEntity e1 = new DocumentEntity(1L, 2L, "a", "/p", now);
        DocumentEntity e2 = new DocumentEntity(1L, 2L, "a", "/p", now);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        assertThat(e1.toString()).contains("DocumentEntity");
    }
}
