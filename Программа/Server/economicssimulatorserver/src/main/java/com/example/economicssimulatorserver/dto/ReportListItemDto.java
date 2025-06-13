package com.example.economicssimulatorserver.dto;

import java.time.OffsetDateTime;

/**
 * Краткая информация об отчёте для отображения в списке.
 *
 * @param id        идентификатор отчёта
 * @param name      название отчёта
 * @param modelName название модели, по которой создан отчёт
 * @param path      путь к файлу отчёта
 * @param createdAt дата создания
 * @param language  язык отчёта
 */
public record ReportListItemDto(
        Long id,
        String name,
        String modelName,
        String path,
        OffsetDateTime createdAt,
        String language
) {}
