package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.entity.UserModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelParameterRepository;
import com.example.economicssimulatorserver.repository.UserModelParameterRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис управления параметрами экономических моделей.
 */
@Service
@RequiredArgsConstructor
public class ModelParameterService {

    private final ModelParameterRepository modelParameterRepository;
    private final EconomicModelRepository economicModelRepository;
    private final UserModelParameterRepository userModelParameterRepository;
    private final ModelParameterRepository economicModelParameterRepository;
    private final UserRepository userRepository;

    /**
     * Возвращает параметры модели для указанного пользователя.
     * При отсутствии пользовательских значений создаёт их.
     *
     * @param modelId идентификатор модели
     * @param userId  идентификатор пользователя
     * @return список параметров
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
     * Обновляет пользовательское значение параметра модели.
     *
     * @param paramId идентификатор параметра
     * @param dto     новые данные
     * @param userId  идентификатор пользователя
     * @return обновлённый параметр
     */
    @Transactional
    public ModelParameterDto updateParameter(Long paramId, ModelParameterDto dto, Long userId) {
        Optional<UserModelParameter> userParamOpt = userModelParameterRepository.findByUserIdAndParameterId(userId, paramId);
        UserModelParameter userParam;
        if (userParamOpt.isPresent()) {
            userParam = userParamOpt.get();
            userParam.setValue(dto.paramValue());
        } else {
            userParam = new UserModelParameter();
            userParam.setUser(userRepository.getReferenceById(userId));
            userParam.setModel(economicModelRepository.getReferenceById(dto.modelId()));
            userParam.setParameter(modelParameterRepository.getReferenceById(paramId));
            userParam.setValue(dto.paramValue());
        }
        userModelParameterRepository.save(userParam);

        ModelParameter param = modelParameterRepository.findById(paramId)
                .orElseThrow(() -> new LocalizedException("error.parameter_not_found"));
        return toDtoWithValue(param, userParam.getValue());
    }

    /**
     * Преобразует параметр и заданное значение в DTO.
     */
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

}
