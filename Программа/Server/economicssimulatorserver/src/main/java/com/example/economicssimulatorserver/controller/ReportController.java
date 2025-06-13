package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.ReportCreateRequestDto;
import com.example.economicssimulatorserver.dto.ReportListItemDto;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления отчётами, созданными пользователями.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    /**
     * Возвращает идентификатор текущего пользователя.
     *
     * @return id пользователя
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new LocalizedException("error.user_not_found"))
                    .getId();
        }
        throw new LocalizedException("error.user_not_authenticated");
    }

    /**
     * Возвращает имя текущего пользователя.
     *
     * @return имя пользователя
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new LocalizedException("error.user_not_authenticated");
    }

    /**
     * Создаёт отчёт на основе проведённых расчётов и сообщений LLM.
     *
     * @param dto параметры нового отчёта
     * @return созданный отчёт
     */
    @PostMapping
    public ResponseEntity<ReportListItemDto> createReport(@RequestBody ReportCreateRequestDto dto) throws Exception {
        Long userId = getCurrentUserId();
        String username = getCurrentUsername();
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

    /**
     * Получает список отчётов текущего пользователя.
     *
     * @return список отчётов
     */
    @GetMapping
    public ResponseEntity<List<ReportListItemDto>> getReportsForUser() {
        Long userId = getCurrentUserId();
        List<ReportListItemDto> list = reportService.getReportsForUser(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * Скачивает PDF-файл отчёта.
     *
     * @param reportId идентификатор отчёта
     * @return содержимое PDF-файла
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable("id") Long reportId) throws Exception {
        Long userId = getCurrentUserId();
        byte[] pdfBytes = reportService.downloadReport(reportId, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Удаляет отчёт пользователя.
     *
     * @param reportId идентификатор отчёта
     * @return пустой ответ при успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("id") Long reportId) throws Exception {
        Long userId = getCurrentUserId();
        reportService.deleteReport(reportId, userId);
        return ResponseEntity.noContent().build();
    }
}
