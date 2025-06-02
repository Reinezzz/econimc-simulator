package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.solver.EconomicModelSolver;
import com.example.economicssimulatorserver.solver.SolverFactory;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelCalculationService {

    private final EconomicModelRepository economicModelRepository;
    private final ModelResultRepository modelResultRepository;
    private final SolverFactory solverFactory;

    @Transactional
    public CalculationResponseDto calculate(CalculationRequestDto request) {
        EconomicModel model = economicModelRepository.findById(request.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + request.modelId()));

        EconomicModelSolver solver = solverFactory.getSolver(request.modelType());
        if (solver == null)
            throw new IllegalArgumentException("Solver not found for modelType: " + request.modelType());

        // Получение результата вычислений
        CalculationResponseDto resultDto = solver.solve(request);

        // Сохраняем результат в БД (опционально)
        if (resultDto.result() != null) {
            ModelResult modelResult = new ModelResult();
            modelResult.setModel(model);
            modelResult.setResultType(resultDto.result().resultType());
            modelResult.setResultData(resultDto.result().resultData());
            modelResultRepository.save(modelResult);
        }

        return resultDto;
    }
}