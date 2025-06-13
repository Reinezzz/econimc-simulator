package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.Report;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.ReportRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.MessageSource;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock ReportRepository reportRepository;
    @Mock MinioClient minioClient;
    @Mock MessageSource messageSource;
    @InjectMocks ReportService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service.reportBucket = "reports";
    }

    @Test
    void getReportsForUser_success() {
        Report report = new Report(1L, 2L, 3L, "n", "m", "p", OffsetDateTime.now(), "ru", "{}", "{}", "[]");
        when(reportRepository.findByUserId(2L)).thenReturn(List.of(report));
        List<ReportListItemDto> result = service.getReportsForUser(2L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("n");
    }

    @Test
    void downloadReport_wrongUser_throws() {
        Report report = new Report(1L, 2L, 3L, "n", "m", "p", OffsetDateTime.now(), "ru", "{}", "{}", "[]");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        assertThatThrownBy(() -> service.downloadReport(1L, 3L)).isInstanceOf(LocalizedException.class);
    }

    @Test
    void deleteReport_notFound_throws() {
        when(reportRepository.findById(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteReport(8L, 3L)).isInstanceOf(LocalizedException.class);
    }
}
