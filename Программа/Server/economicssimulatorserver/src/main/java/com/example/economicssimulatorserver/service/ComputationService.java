package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ComputationResultDto;

/**
 * Сервис для запуска вычислений по математической модели.
 */
public interface ComputationService {

    /**
     * Запускает вычисление по модели.
     *
     * @param mathModelId идентификатор математической модели
     * @return DTO с результатом вычислений
     */
    ComputationResultDto compute(Long mathModelId);

    /**
     * Получает результат вычислений по идентификатору результата.
     *
     * @param computationResultId идентификатор результата
     * @return DTO результата вычислений
     */
    ComputationResultDto getResult(Long computationResultId);
}
