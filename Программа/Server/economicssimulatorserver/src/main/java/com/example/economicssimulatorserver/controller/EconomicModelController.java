package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
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

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class EconomicModelController {

    private final EconomicModelService economicModelService;
    private final ModelParameterService modelParameterService;
    private final ModelCalculationService modelCalculationService;
    private final UserRepository userRepository; // нужен для поиска id по username

    // Метод для получения id текущего пользователя из security context
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username))
                    .getId();
        }
        throw new IllegalStateException("User not authenticated");
    }

    @GetMapping("/")
    public ResponseEntity<List<EconomicModelDto>> getAllModels() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(economicModelService.getAllModels(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EconomicModelDto> getModelById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(economicModelService.getModelById(id, userId));
    }

    @PostMapping
    public ResponseEntity<EconomicModelDto> createModel(@RequestBody EconomicModelDto dto) {
        return ResponseEntity.ok(economicModelService.createModel(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EconomicModelDto> updateModel(@PathVariable Long id, @RequestBody EconomicModelDto dto) {
        return ResponseEntity.ok(economicModelService.updateModel(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        economicModelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }

    // === ModelParameter для пользователя ===

    @GetMapping("/{modelId}/parameters")
    public ResponseEntity<List<ModelParameterDto>> getUserParameters(
            @PathVariable Long modelId
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelParameterService.getParametersByModelId(modelId, userId));
    }

    // === ModelParameter CRUD (общие параметры — для админки/конструктора) ===

    @PostMapping("/{modelId}/parameters")
    public ResponseEntity<ModelParameterDto> createParameter(@PathVariable Long modelId, @RequestBody ModelParameterDto dto) {
        return ResponseEntity.ok(modelParameterService.createParameter(modelId, dto));
    }

    @PutMapping("/parameters/{paramId}")
    public ResponseEntity<ModelParameterDto> updateParameter(@PathVariable Long paramId, @RequestBody ModelParameterDto dto) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelParameterService.updateParameter(paramId, dto, userId));
    }

    @DeleteMapping("/parameters/{paramId}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long paramId) {
        modelParameterService.deleteParameter(paramId);
        return ResponseEntity.noContent().build();
    }

    // === Calculation ===

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponseDto> calculate(@RequestBody CalculationRequestDto request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(modelCalculationService.calculate(request, userId));
    }
}

