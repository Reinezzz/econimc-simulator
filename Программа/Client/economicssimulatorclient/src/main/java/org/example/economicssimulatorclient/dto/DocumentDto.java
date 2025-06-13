package org.example.economicssimulatorclient.dto;

import java.time.LocalDateTime;

/**
 * Данные о документе, загруженном пользователем.
 * Используется при работе со списком файлов и их загрузкой.
 *
 * @param id         идентификатор документа
 * @param userId     идентификатор владельца
 * @param name       имя файла
 * @param path       относительный путь к файлу
 * @param uploadedAt дата и время загрузки
 */
public record DocumentDto(
        Long id,
        Long userId,
        String name,
        String path,
        LocalDateTime uploadedAt
) {}
