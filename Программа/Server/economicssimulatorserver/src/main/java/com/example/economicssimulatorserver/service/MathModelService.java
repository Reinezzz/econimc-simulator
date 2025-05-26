package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;

import java.util.List;

/**
 * Сервис для управления математическими моделями пользователя.
 */
public interface MathModelService {

    /**
     * Создает новую математическую модель.
     *
     * @param dto DTO для создания модели
     * @return созданная модель
     */
    MathModelDto createMathModel(MathModelCreateDto dto);

    /**
     * Обновляет существующую модель.
     *
     * @param modelId идентификатор модели
     * @param dto DTO для обновления
     * @return обновленная модель
     */
    MathModelDto updateMathModel(Long modelId, MathModelUpdateDto dto);

    /**
     * Удаляет модель по идентификатору.
     *
     * @param modelId идентификатор модели
     */
    void deleteMathModel(Long modelId);

    /**
     * Возвращает модель по идентификатору.
     *
     * @param modelId идентификатор модели
     * @return DTO модели
     */
    MathModelDto getMathModel(Long modelId);

    /**
     * Возвращает список моделей для пользователя.
     *
     * @param username идентификатор пользователя
     * @return список DTO моделей
     */
    List<MathModelDto> getMathModelsByUser(String username);
}
