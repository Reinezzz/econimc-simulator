package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ComputationResultDto;
import com.example.economicssimulatorserver.enums.ComputationStatus;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.ComputationResult;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.repository.ComputationResultRepository;
import com.example.economicssimulatorserver.repository.MathModelRepository;
import com.example.economicssimulatorserver.solver.SolverFactory;
import com.example.economicssimulatorserver.solver.SolverResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Реализация сервиса вычислений по математическим моделям.
 */
@Service
@RequiredArgsConstructor
public class ComputationServiceImpl implements ComputationService {

    private final MathModelRepository mathModelRepository;
    private final ComputationResultRepository computationResultRepository;
    private final SolverFactory solverFactory;

    /**
     * Запускает вычисление по модели.
     *
     * @param mathModelId идентификатор математической модели
     * @return DTO с результатом вычислений
     * @throws LocalizedException если модель не найдена или вычисление не удалось
     */
    @Override
    @Transactional
    public ComputationResultDto compute(Long mathModelId) {
        MathModel model = mathModelRepository.findById(mathModelId)
                .orElseThrow(() -> new LocalizedException("error.model_not_found"));

        ComputationResult result = new ComputationResult();
        result.setMathModel(model);
        result.setStartedAt(LocalDateTime.now());
        result.setStatus(ComputationStatus.RUNNING);
        result = computationResultRepository.save(result);

        try {
            // Получаем вычислитель через фабрику и запускаем расчёт
            SolverResult solverResult = solverFactory
                    .getSolver(model.getModelType())
                    .solve(model);

            result.setFinishedAt(LocalDateTime.now());
            result.setStatus(ComputationStatus.SUCCESS);
            result.setResultData(solverResult.resultJson());
            result.setError(null);

        } catch (LocalizedException lex) {
            // Обрабатываем наши локализованные ошибки из solver-ов
            result.setFinishedAt(LocalDateTime.now());
            result.setStatus(ComputationStatus.FAILED);
            result.setResultData(null);
            result.setError(lex.getMessage());
            computationResultRepository.save(result);
            throw lex; // Прокидываем дальше, чтобы ApiExceptionHandler вернул клиенту правильный ответ

        } catch (Exception ex) {
            // Внутренняя ошибка (например, бага или неучтённая ситуация)
            result.setFinishedAt(LocalDateTime.now());
            result.setStatus(ComputationStatus.FAILED);
            result.setResultData(null);
            result.setError("error.calculation_failed");
            computationResultRepository.save(result);
            throw new LocalizedException("error.calculation_failed");
        }

        ComputationResult saved = computationResultRepository.save(result);
        return toDto(saved);
    }

    /**
     * Получает результат вычислений по идентификатору результата.
     *
     * @param computationResultId идентификатор результата
     * @return DTO результата вычислений
     * @throws LocalizedException если результат не найден
     */
    @Override
    @Transactional(readOnly = true)
    public ComputationResultDto getResult(Long computationResultId) {
        ComputationResult result = computationResultRepository.findById(computationResultId)
                .orElseThrow(() -> new LocalizedException("error.result_not_found"));
        return toDto(result);
    }

    /**
     * Маппинг ComputationResult в ComputationResultDto.
     *
     * @param result сущность результата
     * @return DTO результата
     */
    private ComputationResultDto toDto(ComputationResult result) {
        return new ComputationResultDto(
                result.getId(),
                result.getMathModel().getId(),
                result.getStartedAt(),
                result.getFinishedAt(),
                result.getStatus().name(),
                result.getResultData(),
                result.getError()
        );
    }
}
