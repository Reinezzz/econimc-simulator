package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.Report;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис формирования PDF-отчётов и работы с ними в хранилище.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MinioClient minioClient;
    private final MessageSource messageSource;

    @Value("${minio.bucket.reports}")
    String reportBucket;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Создаёт PDF-отчёт и сохраняет его в хранилище.
     *
     * @param userId   идентификатор пользователя
     * @param username имя пользователя
     * @param dto      данные отчёта
     * @return информация о созданном отчёте
     */
    @Transactional
    public ReportListItemDto createReport(Long userId, String username, ReportCreateRequestDto dto) throws Exception {
        String filename = UUID.randomUUID() + ".pdf";
        String minioPath = userId + "/" + filename;

        byte[] pdfBytes = generatePdf(dto, username);

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

    /**
     * Возвращает список отчётов пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список отчётов
     */
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

    /**
     * Загружает готовый отчёт пользователя.
     *
     * @param reportId идентификатор отчёта
     * @param userId   идентификатор пользователя
     * @return байтовый массив PDF
     */
    public byte[] downloadReport(Long reportId, Long userId) throws Exception {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new LocalizedException("error.report_not_found"));
        if (!report.getUserId().equals(userId)) {
            throw new LocalizedException("error.report_access_denied");
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

    /**
     * Удаляет отчёт пользователя из хранилища и базы данных.
     *
     * @param reportId идентификатор отчёта
     * @param userId   идентификатор пользователя
     */
    @Transactional
    public void deleteReport(Long reportId, Long userId) throws Exception {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new LocalizedException("error.report_not_found"));
        if (!report.getUserId().equals(userId)) {
            throw new LocalizedException("error.report_access_denied");
        }
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(reportBucket)
                        .object(report.getPath())
                        .build()
        );
        reportRepository.delete(report);
    }

    /**
     * Формирует PDF-файл отчёта на основе переданных данных.
     */
    private byte[] generatePdf(ReportCreateRequestDto dto, String username) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            InputStream fontStream = getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf");
            if (fontStream == null) throw new IOException("Roboto-Medium.ttf not found in resources/fonts/");
            byte[] fontBytes = fontStream.readAllBytes();
            com.itextpdf.kernel.font.PdfFont font = com.itextpdf.kernel.font.PdfFontFactory.createFont(fontBytes, com.itextpdf.io.font.PdfEncodings.IDENTITY_H);

            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
            Locale locale = Locale.forLanguageTag(dto.language());

            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.user", new Object[]{username}, locale)).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.date", new Object[]{OffsetDateTime.now().toString()}, locale)).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.language", new Object[]{dto.language()}, locale)).setFont(font));
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.parameters", null, locale)).setFont(font));
            for (ModelParameterDto param : dto.parameters()) {
                String descr = param.description() == null ? "" : localizedValue(param.description());
                document.add(new com.itextpdf.layout.element.Paragraph(
                        localizedValue(param.paramName()) + ": " + param.paramValue() +
                                (descr.isBlank() ? "" : " (" + descr + ")")
                ).setFont(font));
            }
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.result", null, locale)).setFont(font));
            if (dto.parsedResult() != null && !dto.parsedResult().isBlank()) {
                document.add(new com.itextpdf.layout.element.Paragraph(dto.parsedResult()).setFont(font));
            } else {
                document.add(new com.itextpdf.layout.element.Paragraph(
                        messageSource.getMessage("pdf.none", null, locale)).setFont(font));
            }
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

            document.add(new com.itextpdf.layout.element.Paragraph(
                    messageSource.getMessage("pdf.charts", null, locale)).setFont(font));
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
                document.add(new com.itextpdf.layout.element.Paragraph(
                        messageSource.getMessage("pdf.llm_responses", null, locale)).setFont(font));
                for (LlmChatResponseDto msg : dto.llmMessages()) {
                    document.add(new com.itextpdf.layout.element.Paragraph("A: " + msg.assistantMessage()).setFont(font));
                }
            }

            document.close();
            return baos.toByteArray();
        }
    }

    /**
     * Возвращает часть строки в зависимости от текущей локали.
     */
    public static String localizedValue(String line) {
        String[] parts = line.split("\\^", 2);
        if (parts.length == 1) return line;
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ru")) return parts[0].trim();
        else return parts[1].trim();
    }


}
