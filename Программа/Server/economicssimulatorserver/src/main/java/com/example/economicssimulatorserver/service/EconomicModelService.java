package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.EconomicModelDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис управления экономическими моделями и их параметрами.
 */
@Service
@RequiredArgsConstructor
public class EconomicModelService {

    private final EconomicModelRepository economicModelRepository;
    private final ModelParameterService modelParameterService;

    /**
     * Возвращает все экономические модели с параметрами пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список моделей
     */
    @Transactional()
    @Cacheable(value = "models", key = "#userId")
    public List<EconomicModelDto> getAllModels(Long userId) {
        return economicModelRepository.findAll().stream()
                .map(model -> toDto(model, userId))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает модель по идентификатору.
     *
     * @param id     идентификатор модели
     * @param userId идентификатор пользователя для загрузки параметров
     * @return найденная модель
     */
    @Transactional(readOnly = true)
    public EconomicModelDto getModelById(Long id, Long userId) {
        EconomicModel model = economicModelRepository.findById(id)
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));
        return toDto(model, userId);
    }

    /**
     * Преобразует сущность модели в DTO вместе с параметрами и результатами.
     *
     * @param model  модель БД
     * @param userId идентификатор пользователя для загрузки параметров
     * @return DTO модели
     */
    private EconomicModelDto toDto(EconomicModel model, Long userId) {
        List<ModelParameterDto> paramDtos =
                (userId != null)
                        ? modelParameterService.getParametersByModelId(model.getId(), userId)
                        : model.getParameters() != null
                        ? model.getParameters().stream().map(this::toDto).collect(Collectors.toList())
                        : List.of();

        List<ModelResultDto> resultDtos = model.getResults() != null
                ? model.getResults().stream().map(this::toDto).collect(Collectors.toList())
                : List.of();

        return new EconomicModelDto(
                model.getId(),
                model.getModelType(),
                model.getName(),
                model.getDescription(),
                paramDtos,
                resultDtos,
                model.getCreatedAt() != null ? model.getCreatedAt().toString() : null,
                model.getUpdatedAt() != null ? model.getUpdatedAt().toString() : null,
                model.getFormula()
        );
    }

    /**
     * Преобразует сущность параметра в DTO.
     *
     * @param param сущность параметра
     * @return DTO параметра
     */
    private ModelParameterDto toDto(ModelParameter param) {
        return new ModelParameterDto(
                param.getId(),
                param.getModel().getId(),
                param.getParamName(),
                param.getParamType(),
                param.getParamValue(),
                param.getDisplayName(),
                param.getDescription(),
                param.getCustomOrder()
        );
    }

    /**
     * Преобразует результат вычислений в DTO.
     *
     * @param result сущность результата
     * @return DTO результата
     */
    private ModelResultDto toDto(ModelResult result) {
        return new ModelResultDto(
                result.getId(),
                result.getModel().getId(),
                result.getResultType(),
                result.getResultData(),
                result.getCalculatedAt() != null ? result.getCalculatedAt().toString() : null
        );
    }
}
