package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.DocumentDto;
import com.example.economicssimulatorserver.entity.DocumentEntity;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.DocumentRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock MinioClient minioClient;
    @Mock DocumentRepository documentRepository;
    @Mock MultipartFile multipartFile;
    @InjectMocks DocumentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadDocument_nonPdf_throws() {
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("b.png");
        assertThatThrownBy(() -> service.uploadDocument(1L, multipartFile, "desc"))
                .isInstanceOf(LocalizedException.class);
    }

    @Test
    void getById_notFound_throws() {
        when(documentRepository.findById(55L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(55L)).isInstanceOf(LocalizedException.class);
    }
}
