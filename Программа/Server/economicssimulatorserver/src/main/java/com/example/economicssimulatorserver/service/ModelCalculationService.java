package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.solver.EconomicModelSolver;
import com.example.economicssimulatorserver.solver.SolverFactory;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModelCalculationService {

    private final EconomicModelRepository economicModelRepository;
    private final ModelResultRepository modelResultRepository;
    private final SolverFactory solverFactory;
    private final UserRepository userRepository;

    @Transactional
    public CalculationResponseDto calculate(CalculationRequestDto request,Long userId) {
        EconomicModel model = economicModelRepository.findById(request.modelId())
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));

        EconomicModelSolver solver = solverFactory.getSolver(request.modelType());
        if (solver == null)
            throw new LocalizedException("error.solver_not_found");

        // Получение результата вычислений
        CalculationResponseDto resultDto = solver.solve(request);

        // Сохраняем результат в БД (уникально для user+model)
        if (resultDto.result() != null) {
            // Найти по userId и modelId
            Optional<ModelResult> existingOpt = modelResultRepository.findByUserIdAndModelId(userId, model.getId());
            ModelResult modelResult;
            if (existingOpt.isPresent()) {
                // Обновить существующий результат
                modelResult = existingOpt.get();
                modelResult.setResultType(resultDto.result().resultType());
                modelResult.setResultData(resultDto.result().resultData());
            } else {
                // Создать новый
                modelResult = new ModelResult();
                modelResult.setModel(model);
                modelResult.setUser(userRepository.getReferenceById(userId)); // обязательно!
                modelResult.setResultType(resultDto.result().resultType());
                modelResult.setResultData(resultDto.result().resultData());
            }
            modelResultRepository.save(modelResult);
        }

        return resultDto;
    }

}