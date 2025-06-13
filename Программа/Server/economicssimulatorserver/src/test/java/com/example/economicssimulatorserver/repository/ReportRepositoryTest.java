package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private ReportRepository repository;

    @Test
    void saveAndFindByUserId() {
        Report report1 = Report.builder()
                .userId(123L)
                .modelId(1L)
                .name("r1")
                .createdAt(OffsetDateTime.now())
                .build();

        Report report2 = Report.builder()
                .userId(123L)
                .modelId(2L)
                .name("r2")
                .createdAt(OffsetDateTime.now())
                .build();

        repository.save(report1);
        repository.save(report2);

        List<Report> reports = repository.findByUserId(123L);
        assertThat(reports).hasSize(2).extracting(Report::getName).containsExactlyInAnyOrder("r1", "r2");
    }
}
