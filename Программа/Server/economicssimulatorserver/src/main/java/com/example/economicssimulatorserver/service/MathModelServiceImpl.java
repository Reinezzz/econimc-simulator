package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.repository.MathModelRepository;
import com.example.economicssimulatorserver.repository.ModelParameterRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.MathModelService;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления математическими моделями.
 */
@Service
@RequiredArgsConstructor
public class MathModelServiceImpl implements MathModelService {

    private final MathModelRepository mathModelRepository;
    private final ModelParameterRepository modelParameterRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MathModelDto createMathModel(MathModelCreateDto dto) {
        validateCreateDto(dto);

        MathModel model = new MathModel();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new LocalizedException("error.user_not_found"));
        model.setUserId(user.getId());
        model.setName(dto.name());
        model.setModelType(ModelType.valueOf(dto.type()));

        MathModel savedModel = mathModelRepository.save(model);

        if (dto.parameters() != null) {
            List<ModelParameter> parameters = dto.parameters().stream()
                    .map(paramDto -> toEntity(paramDto, savedModel))
                    .collect(Collectors.toList());
            modelParameterRepository.saveAll(parameters);
            savedModel.setParameters(parameters);
        }

        return toDto(savedModel);
    }

    @Override
    @Transactional
    public MathModelDto updateMathModel(Long modelId, MathModelUpdateDto dto) {
        MathModel model = mathModelRepository.findById(modelId)
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));

        model.setName(dto.name());
        model.setModelType(ModelType.valueOf(dto.type()));


        modelParameterRepository.deleteAll(model.getParameters());
        if (dto.parameters() != null) {
            List<ModelParameter> parameters = dto.parameters().stream()
                    .map(paramDto -> toEntity(paramDto, model))
                    .collect(Collectors.toList());
            modelParameterRepository.saveAll(parameters);
            model.setParameters(parameters);
        } else {
            model.getParameters().clear();
        }

        MathModel updated = mathModelRepository.save(model);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMathModel(Long modelId) {
        if (!mathModelRepository.existsById(modelId)) {
            throw new LocalizedException("error.model_not_found");
        }
        mathModelRepository.deleteById(modelId);
    }

    @Override
    @Transactional(readOnly = true)
    public MathModelDto getMathModel(Long modelId) {
        MathModel model = mathModelRepository.findById(modelId)
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));
        return toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MathModelDto> getMathModelsByUser(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new LocalizedException("error.user_not_found"));
        List<MathModel> models = mathModelRepository.findByUserId(user.getId());
        return models.stream().map(this::toDto).collect(Collectors.toList());
    }

    private void validateCreateDto(MathModelCreateDto dto) {
        if (dto.name() == null || dto.name().isBlank()) throw new LocalizedException("error.model_name_required");
        if (dto.type() == null) throw new LocalizedException("error.model_type_required");
    }

    private ModelParameter toEntity(ModelParameterCreateDto paramDto, MathModel model) {
        ModelParameter param = new ModelParameter();
        param.setMathModel(model);
        param.setName(paramDto.name());
        param.setParamType(paramDto.paramType());
        param.setValue(paramDto.value());
        param.setDescription(paramDto.description());
        return param;
    }

    private ModelParameter toEntity(ModelParameterUpdateDto paramDto, MathModel model) {
        ModelParameter param = new ModelParameter();
        param.setId(paramDto.id());
        param.setMathModel(model);
        param.setName(paramDto.name());
        param.setParamType(paramDto.paramType());
        param.setValue(paramDto.value());
        param.setDescription(paramDto.description());
        return param;
    }

    private MathModelDto toDto(MathModel model) {
        List<ModelParameterDto> paramDtos = model.getParameters() == null ? List.of() :
                model.getParameters().stream().map(this::toDto).collect(Collectors.toList());

        return new MathModelDto(
                model.getId(),
                model.getName(),
                model.getModelType().name(),
                model.getFormula(),
                paramDtos,
                model.getUserId()

        );
    }

    private ModelParameterDto toDto(ModelParameter param) {
        return new ModelParameterDto(
                param.getId(),
                param.getMathModel().getId(),
                param.getName(),
                param.getParamType(),
                param.getValue(),
                param.getDescription()
        );
    }
}
