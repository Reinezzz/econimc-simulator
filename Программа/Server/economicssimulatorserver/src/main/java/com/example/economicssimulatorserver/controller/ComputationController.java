package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.ComputationResultDto;
import com.example.economicssimulatorserver.service.ComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST-контроллер для запуска вычислений и получения результатов.
 */
@RestController
@RequestMapping("/api/computation")
@RequiredArgsConstructor
public class ComputationController {

    private final ComputationService computationService;

    /**
     * Запускает вычисление по математической модели.
     * @param mathModelId идентификатор модели
     * @return результат вычислений
     */
    @PostMapping("/run/{mathModelId}")
    public ResponseEntity<ComputationResultDto> runComputation(@PathVariable("mathModelId") Long mathModelId, @RequestBody Map<String, String> values) {
        return ResponseEntity.ok(computationService.compute(mathModelId, values));
    }

    /**
     * Получает результат вычислений по id результата.
     * @param resultId идентификатор результата
     * @return результат вычислений
     */
    @GetMapping("/result/{resultId}")
    public ResponseEntity<ComputationResultDto> getResult(@PathVariable("resultId") Long resultId) {
        return ResponseEntity.ok(computationService.getResult(resultId));
    }
}
