package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.service.MathModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления математическими моделями пользователя.
 */
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class MathModelController {

    private final MathModelService mathModelService;

    /**
     * Создаёт новую математическую модель.
     * @param dto DTO для создания модели
     * @return созданная модель
     */
    @PostMapping
    public ResponseEntity<MathModelDto> createModel(@RequestBody MathModelCreateDto dto) {
        return ResponseEntity.ok(mathModelService.createMathModel(dto));
    }

    /**
     * Обновляет существующую модель.
     * @param id идентификатор модели
     * @param dto DTO для обновления
     * @return обновлённая модель
     */
    @PutMapping("/{id}")
    public ResponseEntity<MathModelDto> updateModel(
            @PathVariable("id") Long id,
            @RequestBody MathModelUpdateDto dto
    ) {
        return ResponseEntity.ok(mathModelService.updateMathModel(id, dto));
    }

    /**
     * Удаляет математическую модель.
     * @param id идентификатор модели
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable("id") Long id) {
        mathModelService.deleteMathModel(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает модель по идентификатору.
     * @param id идентификатор модели
     * @return DTO модели
     */
    @GetMapping("/{id}")
    public ResponseEntity<MathModelDto> getModel(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mathModelService.getMathModel(id));
    }

    /**
     * Получает список моделей пользователя.
     * @param accessToken идентификатор пользователя
     * @return список моделей
     */
    @GetMapping
    public ResponseEntity<List<MathModelDto>> getModelsByUser(@RequestParam("userId") String accessToken) {
        return ResponseEntity.ok(mathModelService.getMathModelsByUser(accessToken));
    }
}
