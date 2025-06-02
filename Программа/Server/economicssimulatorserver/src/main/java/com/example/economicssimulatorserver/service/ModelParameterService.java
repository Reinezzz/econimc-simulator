package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelParameterService {

    private final ModelParameterRepository modelParameterRepository;
    private final EconomicModelRepository economicModelRepository;

    @Transactional(readOnly = true)
    public List<ModelParameterDto> getParametersByModelId(Long modelId) {
        return modelParameterRepository.findByModelId(modelId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ModelParameterDto createParameter(Long modelId, ModelParameterDto dto) {
        EconomicModel model = economicModelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        ModelParameter parameter = fromDto(dto);
        parameter.setModel(model);
        ModelParameter saved = modelParameterRepository.save(parameter);
        return toDto(saved);
    }

    @Transactional
    public ModelParameterDto updateParameter(Long paramId, ModelParameterDto dto) {
        ModelParameter parameter = modelParameterRepository.findById(paramId)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + paramId));
        parameter.setParamName(dto.paramName());
        parameter.setParamType(dto.paramType());
        parameter.setParamValue(dto.paramValue());
        parameter.setDisplayName(dto.displayName());
        parameter.setDescription(dto.description());
        parameter.setCustomOrder(dto.customOrder());
        return toDto(parameter);
    }

    @Transactional
    public void deleteParameter(Long paramId) {
        if (!modelParameterRepository.existsById(paramId))
            throw new IllegalArgumentException("Parameter not found: " + paramId);
        modelParameterRepository.deleteById(paramId);
    }

    // ==== entity <-> record ====

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

    private ModelParameter fromDto(ModelParameterDto dto) {
        ModelParameter parameter = new ModelParameter();
        parameter.setId(dto.id());
        parameter.setParamName(dto.paramName());
        parameter.setParamType(dto.paramType());
        parameter.setParamValue(dto.paramValue());
        parameter.setDisplayName(dto.displayName());
        parameter.setDescription(dto.description());
        parameter.setCustomOrder(dto.customOrder());
        return parameter;
    }
}