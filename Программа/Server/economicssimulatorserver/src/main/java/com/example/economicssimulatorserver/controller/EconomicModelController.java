package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.EconomicModelService;
import com.example.economicssimulatorserver.service.ModelParameterService;
import com.example.economicssimulatorserver.service.ModelCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер управления экономическими моделями и их параметрами.
 */
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class EconomicModelController {

    private final EconomicModelService economicModelService;
    private final ModelParameterService modelParameterService;
    private final ModelCalculationService modelCalculationService;
    private final UserRepository userRepository;

    /**
     * Возвращает идентификатор текущего пользователя.
     *
     * @return id пользователя из базы данных
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new LocalizedException("error.user_not_found"))
                    .getId();
        }
        throw new LocalizedException("error.user_not_authenticated");
    }

    /**
     * Получить список моделей для текущего пользователя.
     *
     * @return список моделей в обёртке {@link ResponseEntity}
     */
    @GetMapping("/")
    public ResponseEntity<List<EconomicModelDto>> getAllModels() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(economicModelService.getAllModels(userId));
    }

    /**
     * Получить модель по её идентификатору.
     *
     * @param id идентификатор модели
     * @return найденная модель
     */
    @GetMapping("/{id}")
    public ResponseEntity<EconomicModelDto> getModelById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(economicModelService.getModelById(id, userId));
    }

    /**
     * Получить параметры указанной модели пользователя.
     *
     * @param modelId идентификатор модели
     * @return список параметров модели
     */
    @GetMapping("/{modelId}/parameters")
    public ResponseEntity<List<ModelParameterDto>> getUserParameters(
            @PathVariable Long modelId
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelParameterService.getParametersByModelId(modelId, userId));
    }

    /**
     * Обновление параметра модели.
     *
     * @param paramId идентификатор параметра
     * @param dto     новые значения параметра
     * @return обновлённый параметр
     */
    @PutMapping("/parameters/{paramId}")
    public ResponseEntity<ModelParameterDto> updateParameter(@PathVariable Long paramId, @RequestBody ModelParameterDto dto) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelParameterService.updateParameter(paramId, dto, userId));
    }

    /**
     * Выполнение расчёта модели по переданным параметрам.
     *
     * @param request параметры для расчёта
     * @return результат вычислений модели
     */
    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponseDto> calculate(@RequestBody CalculationRequestDto request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelCalculationService.calculate(request, userId));
    }
}

