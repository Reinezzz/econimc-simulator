package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ComputationResultDto;

import java.util.Map;

/**
 * Сервис для запуска вычислений по математической модели.
 */
public interface ComputationService {

    /**
     * Запускает вычисление по модели.
     *
     * @param mathModelId идентификатор математической модели
     * @param values
     * @return DTO с результатом вычислений
     */
    ComputationResultDto compute(Long mathModelId, Map<String, String> values);

    /**
     * Получает результат вычислений по идентификатору результата.
     *
     * @param computationResultId идентификатор результата
     * @return DTO результата вычислений
     */
    ComputationResultDto getResult(Long computationResultId);
}
