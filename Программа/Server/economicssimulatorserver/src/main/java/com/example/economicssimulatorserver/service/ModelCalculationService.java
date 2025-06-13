package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
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

import java.util.Optional;

/**
 * Сервис выполнения расчётов экономических моделей.
 */
@Service
@RequiredArgsConstructor
public class ModelCalculationService {

    private final EconomicModelRepository economicModelRepository;
    private final ModelResultRepository modelResultRepository;
    private final SolverFactory solverFactory;
    private final UserRepository userRepository;

    /**
     * Выполняет расчёт экономической модели и сохраняет результат для пользователя.
     *
     * @param request данные для расчёта
     * @param userId  идентификатор пользователя
     * @return результат вычислений
     */
    @Transactional
    public CalculationResponseDto calculate(CalculationRequestDto request,Long userId) {
        EconomicModel model = economicModelRepository.findById(request.modelId())
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));

        EconomicModelSolver solver = solverFactory.getSolver(request.modelType());
        if (solver == null)
            throw new LocalizedException("error.solver_not_found");

        CalculationResponseDto resultDto = solver.solve(request);

        if (resultDto.result() != null) {
            Optional<ModelResult> existingOpt = modelResultRepository.findByUserIdAndModelId(userId, model.getId());
            ModelResult modelResult;
            if (existingOpt.isPresent()) {
                modelResult = existingOpt.get();
                modelResult.setResultType(resultDto.result().resultType());
                modelResult.setResultData(resultDto.result().resultData());
            } else {
                modelResult = new ModelResult();
                modelResult.setModel(model);
                modelResult.setUser(userRepository.getReferenceById(userId));
                modelResult.setResultType(resultDto.result().resultType());
                modelResult.setResultData(resultDto.result().resultData());
            }
            modelResultRepository.save(modelResult);
        }

        return resultDto;
    }

}