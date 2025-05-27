package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.ComputationResultDto;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.net.URI;
import java.util.Map;

public class ComputationService extends MainService {

    private static final String MAIN_ENDPOINT = "/api/computation";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + MAIN_ENDPOINT);


    /**
     * Запустить вычисление по id математической модели.
     * Сервер получает только id модели, параметры вычислений он определяет сам (или из параметров модели).
     * Возвращает ComputationResultDto (startedAt/finishedAt придут в ответе, но клиент их не отправляет).
     */
    public ComputationResultDto runComputation(Long mathModelId, Map<Long, String> paramValues) throws Exception {

        return post(baseUri, MAIN_ENDPOINT + "/run/" + mathModelId, paramValues, ComputationResultDto.class, true, null);
    }

    /**
     * Получить результат вычисления по id вычисления (например, для детального просмотра).
     */
    public ComputationResultDto getResultById(Long computationId) throws Exception {
        return get(baseUri, MAIN_ENDPOINT + "/result/" + computationId, ComputationResultDto.class, true, null);
    }
}
