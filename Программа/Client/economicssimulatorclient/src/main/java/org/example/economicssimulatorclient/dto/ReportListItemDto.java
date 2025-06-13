package org.example.economicssimulatorclient.dto;

import java.time.OffsetDateTime;

/**
 * Краткие сведения об отчёте для отображения в списке.
 *
 * @param id        идентификатор отчёта
 * @param name      название отчёта
 * @param modelName название модели
 * @param path      путь к файлу отчёта
 * @param createdAt время создания
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
