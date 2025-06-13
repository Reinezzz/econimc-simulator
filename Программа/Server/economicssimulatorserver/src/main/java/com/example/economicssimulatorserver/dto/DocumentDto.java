package com.example.economicssimulatorserver.dto;

import java.time.LocalDateTime;

/**
 * Информация о загруженном пользователем документе.
 *
 * @param id         идентификатор документа
 * @param userId     идентификатор владельца
 * @param name       исходное имя файла
 * @param path       путь к файлу на сервере
 * @param uploadedAt дата и время загрузки
 */
public record DocumentDto(
        Long id,
        Long userId,
        String name,
        String path,
        LocalDateTime uploadedAt
) {}
