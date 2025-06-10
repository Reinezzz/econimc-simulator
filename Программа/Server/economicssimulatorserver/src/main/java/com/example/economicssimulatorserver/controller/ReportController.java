package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.ReportCreateRequestDto;
import com.example.economicssimulatorserver.dto.ReportListItemDto;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    // Вспомогательный метод для получения id пользователя
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username))
                    .getId();
        }
        throw new IllegalStateException("User not authenticated");
    }

    // Вспомогательный метод для получения username
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IllegalStateException("User not authenticated");
    }

    // --- СОЗДАНИЕ ОТЧЁТА ---
    @PostMapping
    public ResponseEntity<ReportListItemDto> createReport(@RequestBody ReportCreateRequestDto dto) throws Exception {
        Long userId = getCurrentUserId();
        String username = getCurrentUsername();
        // Имя отчета формируется автоматически: modelName + " - " + username
        String name = dto.modelName() + " - " + username;
        ReportCreateRequestDto newDto = new ReportCreateRequestDto(
                dto.modelId(),
                dto.modelName(),
                name,
                dto.language(),
                dto.parameters(),
                dto.result(),
                dto.charts(),
                dto.llmMessages(),
                dto.parsedResult()
        );
        ReportListItemDto created = reportService.createReport(userId, username, newDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // --- ПОЛУЧИТЬ ВСЕ ОТЧЁТЫ ПОЛЬЗОВАТЕЛЯ ---
    @GetMapping
    public ResponseEntity<List<ReportListItemDto>> getReportsForUser() {
        Long userId = getCurrentUserId();
        List<ReportListItemDto> list = reportService.getReportsForUser(userId);
        return ResponseEntity.ok(list);
    }

    // --- СКАЧАТЬ PDF ОТЧЁТА ---
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable("id") Long reportId) throws Exception {
        Long userId = getCurrentUserId();
        byte[] pdfBytes = reportService.downloadReport(reportId, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // --- УДАЛИТЬ ОТЧЁТ ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("id") Long reportId) throws Exception {
        Long userId = getCurrentUserId();
        reportService.deleteReport(reportId, userId);
        return ResponseEntity.noContent().build();
    }
}
