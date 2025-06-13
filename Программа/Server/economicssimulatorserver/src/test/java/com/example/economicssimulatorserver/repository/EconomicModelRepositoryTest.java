package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.EconomicModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EconomicModelRepositoryTest {

    @Autowired
    private EconomicModelRepository repository;

    @Test
    void saveAndFindById() {
        EconomicModel model = EconomicModel.builder()
                .modelType("macro")
                .name("IS-LM")
                .description("desc")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EconomicModel saved = repository.save(model);
        assertThat(saved.getId()).isNotNull();

        EconomicModel found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("IS-LM");
    }
}
