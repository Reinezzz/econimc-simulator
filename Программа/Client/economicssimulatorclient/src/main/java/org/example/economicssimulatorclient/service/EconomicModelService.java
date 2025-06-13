package org.example.economicssimulatorclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис для работы с экономическими моделями и их параметрами на сервере.
 */
public class EconomicModelService extends MainService {

    private static final String BASE_API = "/api/models";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + BASE_API + "/");
    private static final ObjectMapper objectMapper = AppConfig.objectMapper;

    /**
     * Возвращает список всех доступных моделей.
     *
     * @return список моделей
     */
    public List<EconomicModelDto> getAllModels() throws IOException, InterruptedException {
        String endpoint = "";
        EconomicModelDto[] arr = get(baseUri, endpoint, EconomicModelDto[].class, true, null);
        return Arrays.asList(arr);
    }

    /**
     * Получает информацию о модели по её идентификатору.
     *
     * @param id уникальный идентификатор модели
     * @return модель или {@code null}, если не найдено
     */
    public EconomicModelDto getModelById(Long id) throws IOException, InterruptedException {
        String endpoint = String.valueOf(id);
        return get(baseUri, endpoint, EconomicModelDto.class, true, null);
    }

    /**
     * Возвращает параметры указанной модели.
     *
     * @param modelId идентификатор модели
     * @return список параметров
     */
    public List<ModelParameterDto> getParametersByModelId(Long modelId) throws IOException, InterruptedException {
        String endpoint = modelId + "/parameters";
        ModelParameterDto[] arr = get(baseUri, endpoint, ModelParameterDto[].class, true, null);
        return Arrays.asList(arr);
    }

    /**
     * Обновляет параметр модели.
     *
     * @param paramId идентификатор параметра
     * @param dto     новые значения
     * @return обновлённый параметр
     */
    public ModelParameterDto updateParameter(Long paramId, ModelParameterDto dto) throws IOException, InterruptedException {
        String endpoint = "parameters/" + paramId;
        return put(baseUri, endpoint, dto, ModelParameterDto.class, true, null);
    }

    /**
     * Отправляет запрос на расчёт модели.
     *
     * @param request содержит id модели и входные данные
     * @return результат расчёта
     */
    public CalculationResponseDto calculate(CalculationRequestDto request) throws IOException, InterruptedException {
        String endpoint = "calculate";
        return post(baseUri, endpoint, request, CalculationResponseDto.class, true, null);
    }
}
