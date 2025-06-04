package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.entity.UserModelParameter;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelParameterRepository;
import com.example.economicssimulatorserver.repository.UserModelParameterRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelParameterService {

    private final ModelParameterRepository modelParameterRepository;
    private final EconomicModelRepository economicModelRepository;
    private final UserModelParameterRepository userModelParameterRepository;
    private final ModelParameterRepository economicModelParameterRepository;
    private final UserRepository userRepository;

    /**
     * Возвращает параметры с учетом пользовательских значений.
     * Если пользовательских значений еще нет — инициализирует их дефолтными.
     */
    @Transactional
    public List<ModelParameterDto> getParametersByModelId(Long modelId, Long userId) {
        List<ModelParameter> params = modelParameterRepository.findByModelId(modelId);
        List<UserModelParameter> userParams = userModelParameterRepository.findByUserIdAndModelId(userId, modelId);
        Map<Long, UserModelParameter> userValueMap = userParams.stream()
                .collect(Collectors.toMap(
                        up -> up.getParameter().getId(),
                        up -> up
                ));

        List<UserModelParameter> toSave = new ArrayList<>();
        List<ModelParameterDto> result = new ArrayList<>();
        for (ModelParameter param : params) {
            UserModelParameter userParam = userValueMap.get(param.getId());
            if (userParam == null) {
                // Ещё раз убедиться, что нет такого user+param (на всякий случай)
                Optional<UserModelParameter> check = userModelParameterRepository.findByUserIdAndParameterId(userId, param.getId());
                if (check.isPresent()) {
                    userParam = check.get();
                } else {
                    userParam = new UserModelParameter();
                    userParam.setUser(userRepository.getReferenceById(userId));
                    userParam.setModel(economicModelRepository.getReferenceById(modelId));
                    userParam.setParameter(economicModelParameterRepository.getReferenceById(param.getId()));
                    userParam.setValue(param.getParamValue());
                    toSave.add(userParam);
                }
            }
            result.add(toDtoWithValue(param, userParam != null ? userParam.getValue() : param.getParamValue()));
        }
        if (!toSave.isEmpty()) {
            userModelParameterRepository.saveAll(toSave);
        }
        return result;
    }

    /**
     * Админ/конструктор: вернуть дефолтные параметры модели (без user-значений).
     */

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
    public ModelParameterDto updateParameter(Long paramId, ModelParameterDto dto, Long userId) {
        // Найти или создать пользовательский параметр
        Optional<UserModelParameter> userParamOpt = userModelParameterRepository.findByUserIdAndParameterId(userId, paramId);
        UserModelParameter userParam;
        if (userParamOpt.isPresent()) {
            userParam = userParamOpt.get();
            userParam.setValue(dto.paramValue());
        } else {
            // Если пользовательское значение ещё не было инициализировано — создать его
            userParam = new UserModelParameter();
            userParam.setUser(userRepository.getReferenceById(userId));
            userParam.setModel(economicModelRepository.getReferenceById(dto.modelId()));
            userParam.setParameter(modelParameterRepository.getReferenceById(paramId));
            userParam.setValue(dto.paramValue());
        }
        userModelParameterRepository.save(userParam);

        // Возвращаем DTO с новым value
        ModelParameter param = modelParameterRepository.findById(paramId)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + paramId));
        return toDtoWithValue(param, userParam.getValue());
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

    private ModelParameterDto toDtoWithValue(ModelParameter param, String value) {
        return new ModelParameterDto(
                param.getId(),
                param.getModel().getId(),
                param.getParamName(),
                param.getParamType(),
                value,
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
