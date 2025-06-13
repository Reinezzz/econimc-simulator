package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.DocumentDto;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Контроллер управления пользовательскими документами.
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    /**
     * Получение идентификатора текущего пользователя из контекста безопасности.
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
     * Загрузка PDF-документа пользователя.
     *
     * @param file        загружаемый файл
     * @param description необязательное описание документа
     * @return информация о сохранённом документе
     */
    @PostMapping("/upload")
    public DocumentDto uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        Long userId = getCurrentUserId();
        return documentService.uploadDocument(userId, file, description);
    }

    /**
     * Скачивание ранее загруженного документа.
     *
     * @param documentId идентификатор документа
     * @return поток для загрузки PDF-файла
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadDocument(
            @PathVariable("id") Long documentId) {
        Long userId = getCurrentUserId();
        DocumentDto doc = documentService.getUserDocuments(userId).stream()
                .filter(d -> d.id().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new LocalizedException("error.document_not_found"));

        InputStream inputStream = documentService.downloadDocument(documentId, userId);

        String encodedName = URLEncoder.encode(doc.name(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedName)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Удаление документа пользователя.
     *
     * @param documentId идентификатор удаляемого документа
     * @return пустой ответ с кодом 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable("id") Long documentId) {
        Long userId = getCurrentUserId();
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение списка документов текущего пользователя.
     *
     * @return список загруженных документов
     */
    @GetMapping("/")
    public List<DocumentDto> getUserDocuments() {
        Long userId = getCurrentUserId();
        return documentService.getUserDocuments(userId);
    }
}
