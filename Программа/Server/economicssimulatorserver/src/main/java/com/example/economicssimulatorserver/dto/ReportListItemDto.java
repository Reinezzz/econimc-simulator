package com.example.economicssimulatorserver.dto;

import java.time.OffsetDateTime;

public record ReportListItemDto(
        Long id,
        String name,
        String modelName,
        String path,                 // Ключ/путь к pdf-файлу в MinIO
        OffsetDateTime createdAt,    // Дата создания
        String language              // "ru" или "en"
) {}
