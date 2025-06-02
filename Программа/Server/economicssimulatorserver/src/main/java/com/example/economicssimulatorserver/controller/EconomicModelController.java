package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.service.EconomicModelService;
import com.example.economicssimulatorserver.service.ModelParameterService;
import com.example.economicssimulatorserver.service.ModelCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class EconomicModelController {

    private final EconomicModelService economicModelService;
    private final ModelParameterService modelParameterService;
    private final ModelCalculationService modelCalculationService;

    // === EconomicModel CRUD ===

    @GetMapping("/")
    public ResponseEntity<List<EconomicModelDto>> getAllModels() {
        return ResponseEntity.ok(economicModelService.getAllModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EconomicModelDto> getModelById(@PathVariable Long id) {
        return ResponseEntity.ok(economicModelService.getModelById(id));
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

    // === ModelParameter CRUD ===

    @GetMapping("/{modelId}/parameters")
    public ResponseEntity<List<ModelParameterDto>> getParametersByModelId(@PathVariable Long modelId) {
        return ResponseEntity.ok(modelParameterService.getParametersByModelId(modelId));
    }

    @PostMapping("/{modelId}/parameters")
    public ResponseEntity<ModelParameterDto> createParameter(@PathVariable Long modelId, @RequestBody ModelParameterDto dto) {
        return ResponseEntity.ok(modelParameterService.createParameter(modelId, dto));
    }

    @PutMapping("/parameters/{paramId}")
    public ResponseEntity<ModelParameterDto> updateParameter(@PathVariable Long paramId, @RequestBody ModelParameterDto dto) {
        return ResponseEntity.ok(modelParameterService.updateParameter(paramId, dto));
    }

    @DeleteMapping("/parameters/{paramId}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long paramId) {
        modelParameterService.deleteParameter(paramId);
        return ResponseEntity.noContent().build();
    }

    // === Calculation ===

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponseDto> calculate(@RequestBody CalculationRequestDto request) {
        return ResponseEntity.ok(modelCalculationService.calculate(request));
    }
}