package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.EconomicModelDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EconomicModelService {

    private final EconomicModelRepository economicModelRepository;
    private final ModelParameterService modelParameterService;

    @Transactional()
    public List<EconomicModelDto> getAllModels(Long userId) {
        return economicModelRepository.findAll().stream()
                .map(model -> toDto(model, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EconomicModelDto getModelById(Long id, Long userId) {
        EconomicModel model = economicModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + id));
        return toDto(model, userId);
    }

    @Transactional
    public EconomicModelDto createModel(EconomicModelDto dto) {
        EconomicModel model = fromDto(dto);
        EconomicModel saved = economicModelRepository.save(model);
        return toDto(saved, null);
    }

    @Transactional
    public EconomicModelDto updateModel(Long id, EconomicModelDto dto) {
        EconomicModel model = economicModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + id));
        model.setName(dto.name());
        model.setDescription(dto.description());
        model.setModelType(dto.modelType());
        EconomicModel updated = economicModelRepository.save(model);
        return toDto(updated, null);
    }

    @Transactional
    public void deleteModel(Long id) {
        if (!economicModelRepository.existsById(id))
            throw new IllegalArgumentException("Model not found: " + id);
        economicModelRepository.deleteById(id);
    }

    // ==== Преобразование между entity <-> record ====

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
                model.getUpdatedAt() != null ? model.getUpdatedAt().toString() : null
        );
    }

    private EconomicModel fromDto(EconomicModelDto dto) {
        EconomicModel model = new EconomicModel();
        model.setId(dto.id());
        model.setModelType(dto.modelType());
        model.setName(dto.name());
        model.setDescription(dto.description());
        // Параметры и результаты добавляются отдельно после создания
        return model;
    }

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
