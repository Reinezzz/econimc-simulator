package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.DocumentDto;
import com.example.economicssimulatorserver.entity.DocumentEntity;
import com.example.economicssimulatorserver.exception.LocalizedException;
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

/**
 * Сервис работы с пользовательскими документами в хранилище MinIO.
 */
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

    /**
     * Загружает PDF-документ в MinIO и сохраняет информацию о нём в базе.
     *
     * @param userId      идентификатор пользователя
     * @param file        загружаемый файл
     * @param description описание документа
     * @return информация о загруженном документе
     */
    @Transactional
    public DocumentDto uploadDocument(Long userId, MultipartFile file, String description) {
        if (!isPdf(file)) {
            throw new LocalizedException("error.pdf_only");
        }
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );


            DocumentEntity entity = new DocumentEntity();
            entity.setUserId(userId);
            entity.setName(file.getOriginalFilename());
            entity.setPath(filename);
            entity.setUploadedAt(LocalDateTime.now());

            documentRepository.save(entity);

            return toDto(entity);
        } catch (Exception e) {
            throw new LocalizedException("error.document_upload");
        }
    }

    /**
     * Возвращает поток данных запрошенного документа пользователя.
     *
     * @param documentId идентификатор документа
     * @param userId     идентификатор пользователя
     * @return поток для чтения файла
     */
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
            throw new LocalizedException("error.document_download");
        }
    }

    /**
     * Удаляет документ пользователя из MinIO и базы данных.
     *
     * @param documentId идентификатор документа
     * @param userId     идентификатор пользователя
     */
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
        } catch (Exception e) {
            throw new LocalizedException("error.document_delete");
        }
        documentRepository.delete(doc);
    }

    /**
     * Возвращает список документов, принадлежащих пользователю.
     *
     * @param userId идентификатор пользователя
     * @return список документов
     */
    public List<DocumentDto> getUserDocuments(Long userId) {
        return documentRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает документ по идентификатору без проверки владельца.
     *
     * @param documentId идентификатор документа
     * @return найденный документ
     */
    public DocumentDto getById(Long documentId) {
        DocumentEntity entity = documentRepository.findById(documentId)
                .orElseThrow(() -> new LocalizedException("error.document_not_found"));
        return toDto(entity);
    }

    /**
     * Возвращает документ пользователя или выбрасывает исключение при отсутствии доступа.
     *
     * @param docId  идентификатор документа
     * @param userId идентификатор пользователя
     * @return сущность документа
     */
    private DocumentEntity getUserDocumentOrThrow(Long docId, Long userId) {
        return documentRepository.findById(docId)
                .filter(doc -> doc.getUserId().equals(userId))
                .orElseThrow(() -> new LocalizedException("error.document_access_denied"));
    }

    /**
     * Преобразует сущность документа в DTO.
     *
     * @param entity сущность документа
     * @return DTO документа
     */
    private DocumentDto toDto(DocumentEntity entity) {
        return new DocumentDto(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getPath(),
                entity.getUploadedAt()
        );
    }

    /**
     * Проверяет, является ли файл PDF-документом.
     *
     * @param file загружаемый файл
     * @return {@code true}, если файл PDF
     */
    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        if ("application/pdf".equalsIgnoreCase(contentType)) return true;

        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }
}
