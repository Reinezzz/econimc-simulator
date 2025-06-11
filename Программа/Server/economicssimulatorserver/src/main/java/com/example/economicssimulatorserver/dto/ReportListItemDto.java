package com.example.economicssimulatorserver.dto;

import java.time.OffsetDateTime;

public record ReportListItemDto(
        Long id,
        String name,
        String modelName,
        String path,
        OffsetDateTime createdAt,
        String language
) {}
