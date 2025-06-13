package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.DocumentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    @DisplayName("Save and findByUserId")
    void saveAndFindByUserId() {
        DocumentEntity doc1 = DocumentEntity.builder()
                .userId(42L)
                .name("doc1.pdf")
                .path("/files/1.pdf")
                .uploadedAt(LocalDateTime.now())
                .build();

        DocumentEntity doc2 = DocumentEntity.builder()
                .userId(42L)
                .name("doc2.pdf")
                .path("/files/2.pdf")
                .uploadedAt(LocalDateTime.now())
                .build();

        documentRepository.save(doc1);
        documentRepository.save(doc2);

        List<DocumentEntity> userDocs = documentRepository.findByUserId(42L);
        assertThat(userDocs).hasSize(2).extracting(DocumentEntity::getName).containsExactlyInAnyOrder("doc1.pdf", "doc2.pdf");
    }
}
