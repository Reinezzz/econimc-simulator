package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.DocumentDto;
import com.example.economicssimulatorserver.entity.DocumentEntity;
import com.example.economicssimulatorserver.repository.DocumentRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final MinioClient minioClient;
    private final DocumentRepository documentRepository;

    @Value("${minio.bucket:documents}")
    private String bucket;

    public DocumentService(MinioClient minioClient, DocumentRepository documentRepository) {
        this.minioClient = minioClient;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public DocumentDto uploadDocument(Long userId, MultipartFile file, String description) {
        if (!isPdf(file)) {
            throw new IllegalArgumentException("Можно загружать только PDF-файлы.");
        }
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // Загрузка в MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Сохраняем метаинфу в БД
            DocumentEntity entity = new DocumentEntity();
            entity.setUserId(userId);
            entity.setName(file.getOriginalFilename());
            entity.setPath(filename);
            entity.setUploadedAt(LocalDateTime.now());

            documentRepository.save(entity);

            return toDto(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла", e);
        }
    }

    public InputStream downloadDocument(Long documentId, Long userId) {
        DocumentEntity doc = getUserDocumentOrThrow(documentId, userId);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(doc.getPath())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла", e);
        }
    }

    @Transactional
    public void deleteDocument(Long documentId, Long userId) {
        DocumentEntity doc = getUserDocumentOrThrow(documentId, userId);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(doc.getPath())
                            .build()
            );
        } catch (MinioException ignore) {
            // Файл мог быть уже удалён из minio — игнорируем
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления файла из хранилища", e);
        }
        documentRepository.delete(doc);
    }

    public List<DocumentDto> getUserDocuments(Long userId) {
        return documentRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DocumentDto getById(Long documentId) {
        DocumentEntity entity = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Документ не найден: " + documentId));
        return toDto(entity);
    }

    private DocumentEntity getUserDocumentOrThrow(Long docId, Long userId) {
        return documentRepository.findById(docId)
                .filter(doc -> doc.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Документ не найден или нет доступа"));
    }

    private DocumentDto toDto(DocumentEntity entity) {
        return new DocumentDto(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getPath(),
                entity.getUploadedAt()
        );
    }

    private boolean isPdf(MultipartFile file) {
        // 1. Проверка по Content-Type
        String contentType = file.getContentType();
        if ("application/pdf".equalsIgnoreCase(contentType)) return true;

        // 2. Проверка по расширению
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }
}
