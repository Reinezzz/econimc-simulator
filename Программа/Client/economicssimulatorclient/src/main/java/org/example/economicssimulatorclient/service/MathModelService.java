package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class MathModelService extends MainService {

    private static final String MAIN_ENDPOINT = "/api/models";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + MAIN_ENDPOINT);


    /**
     * Получить все модели пользователя.
     */
    public List<MathModelDto> getAllModels() throws Exception {
        MathModelDto[] arr = get(baseUri, MAIN_ENDPOINT, MathModelDto[].class, true, null);
        return Arrays.asList(arr);
    }

    /**
     * Получить модель по id.
     */
    public MathModelDto getModelById(Long id) throws Exception {
        return get(baseUri, MAIN_ENDPOINT + "/" + id, MathModelDto.class, true, null);
    }

    /**
     * Создать новую модель.
     */
    public MathModelDto createModel(MathModelCreateDto dto) throws Exception {
        return post(baseUri, MAIN_ENDPOINT, dto, MathModelDto.class, true, null);
    }

    /**
     * Обновить существующую модель.
     */
    public MathModelDto updateModel(MathModelUpdateDto dto) throws Exception {
        return put(baseUri, MAIN_ENDPOINT + "/" + dto.id(), dto, MathModelDto.class, true, null);
    }

    /**
     * Удалить модель.
     */
    public void deleteModel(Long id) throws Exception {
        delete(baseUri, MAIN_ENDPOINT + "/" + id, true, null);
    }
}
