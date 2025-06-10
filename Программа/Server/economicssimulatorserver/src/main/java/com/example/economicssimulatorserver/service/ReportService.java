package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.Report;
import com.example.economicssimulatorserver.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket.reports}")
    private String reportBucket;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- СОЗДАНИЕ ОТЧЕТА ---
    @Transactional
    public ReportListItemDto createReport(Long userId, String username, ReportCreateRequestDto dto) throws Exception {
        String filename = UUID.randomUUID() + ".pdf";
        String minioPath = userId + "/" + filename;

        // Генерируем PDF
        byte[] pdfBytes = generatePdf(dto, username);

        // Заливаем PDF в Minio
        try (ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(reportBucket)
                            .object(minioPath)
                            .stream(bis, pdfBytes.length, -1)
                            .contentType("application/pdf")
                            .build()
            );
        }

        // Сохраняем отчет в БД (всё сериализуем в строки)
        String paramsJson = objectMapper.writeValueAsString(dto.parameters());
        String resultJson = objectMapper.writeValueAsString(dto.result());
        String llmMessagesJson = objectMapper.writeValueAsString(dto.llmMessages());

        Report report = new Report(
                null,
                userId,
                dto.modelId(),
                dto.name(),
                dto.modelName(),
                minioPath,
                OffsetDateTime.now(),
                dto.language(),
                paramsJson,
                resultJson,
                llmMessagesJson
        );
        report = reportRepository.save(report);

        return new ReportListItemDto(
                report.getId(),
                report.getName(),
                report.getModelName(),
                report.getPath(),
                report.getCreatedAt(),
                report.getLanguage()
        );
    }

    // --- ПОЛУЧЕНИЕ СПИСКА ОТЧЕТОВ ПОЛЬЗОВАТЕЛЯ ---
    public List<ReportListItemDto> getReportsForUser(Long userId) {
        return reportRepository.findByUserId(userId).stream()
                .map(report -> new ReportListItemDto(
                        report.getId(),
                        report.getName(),
                        report.getModelName(),
                        report.getPath(),
                        report.getCreatedAt(),
                        report.getLanguage()
                ))
                .collect(Collectors.toList());
    }

    // --- СКАЧИВАНИЕ ОТЧЕТА (pdf как bytes) ---
    public byte[] downloadReport(Long reportId, Long userId) throws Exception {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        if (!report.getUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(reportBucket)
                            .object(report.getPath())
                            .build()
            ).transferTo(baos);
            return baos.toByteArray();
        }
    }

    // --- УДАЛЕНИЕ ОТЧЕТА ---
    @Transactional
    public void deleteReport(Long reportId, Long userId) throws Exception {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        if (!report.getUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        // Удалить из Minio
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(reportBucket)
                        .object(report.getPath())
                        .build()
        );
        // Удалить из БД
        reportRepository.delete(report);
    }

    // --- PDF-ГЕНЕРАЦИЯ ---
    private byte[] generatePdf(ReportCreateRequestDto dto, String username) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            // === Подключаем Roboto-Medium.ttf из ресурсов ===
            // Путь к ttf должен быть src/main/resources/fonts/Roboto-Medium.ttf
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf");
            if (fontStream == null) throw new IOException("Roboto-Medium.ttf not found in resources/fonts/");
            byte[] fontBytes = fontStream.readAllBytes();
            com.itextpdf.kernel.font.PdfFont font = com.itextpdf.kernel.font.PdfFontFactory.createFont(fontBytes, com.itextpdf.io.font.PdfEncodings.IDENTITY_H);

            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

            document.add(new com.itextpdf.layout.element.Paragraph(dto.modelName() + " / " + dto.name())
                    .setFont(font).setBold());
            document.add(new com.itextpdf.layout.element.Paragraph("Пользователь: " + username).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph("Дата: " + OffsetDateTime.now().toString()).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph("Язык: " + dto.language()).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            // Параметры с описанием
            document.add(new com.itextpdf.layout.element.Paragraph("Параметры:").setFont(font));
            for (ModelParameterDto param : dto.parameters()) {
                String descr = param.description() == null ? "" : param.description();
                document.add(new com.itextpdf.layout.element.Paragraph(
                        param.paramName() + ": " + param.paramValue() +
                                (descr.isBlank() ? "" : " (" + descr + ")")
                ).setFont(font));
            }
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            // Парсенный результат (текст)
            document.add(new com.itextpdf.layout.element.Paragraph("Результат:").setFont(font));
            if (dto.parsedResult() != null && !dto.parsedResult().isBlank()) {
                document.add(new com.itextpdf.layout.element.Paragraph(dto.parsedResult()).setFont(font));
            } else {
                document.add(new com.itextpdf.layout.element.Paragraph("—").setFont(font));
            }
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            // Все графики
            document.add(new com.itextpdf.layout.element.Paragraph("Графики:").setFont(font));
            if (dto.charts() != null && !dto.charts().isEmpty()) {
                for (ReportChartImageDto chart : dto.charts()) {
                    byte[] imgBytes = Base64.getDecoder().decode(chart.imageBase64());
                    com.itextpdf.io.image.ImageData imageData = com.itextpdf.io.image.ImageDataFactory.create(imgBytes);
                    document.add(new com.itextpdf.layout.element.Image(imageData).scaleToFit(450, 300));
                    document.add(new com.itextpdf.layout.element.Paragraph(chart.chartType()).setFont(font));
                }
            }
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            if (dto.llmMessages() != null && !dto.llmMessages().isEmpty()) {
                document.add(new com.itextpdf.layout.element.Paragraph("Ответы LLM:").setFont(font));
                for (LlmChatResponseDto msg : dto.llmMessages()) {
                    document.add(new com.itextpdf.layout.element.Paragraph("A: " + msg.assistantMessage()).setFont(font));
                }
            }

            document.close();
            return baos.toByteArray();
        }
    }


}
