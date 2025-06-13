package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер взаимодействия с LLM-сервисом для обработки запросов пользователей.
 */
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LlmController {

    private final LlmService llmService;
    private final EconomicModelService modelService;
    private final DocumentService documentService;
    private final ModelParameterService parameterService;
    private final ModelCalculationService calculationService;
    private final UserRepository userRepository;

    /**
     * Получение id текущего пользователя.
     *
     * @return идентификатор пользователя
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
     * Извлекает параметры модели из документа с помощью LLM.
     *
     * @param req запрос с идентификаторами модели и документа
     * @return найденные параметры и текстовые подсказки
     */
    @PostMapping("/extract-parameters")
    public ResponseEntity<LlmParameterExtractionResponseDto> extractParameters(
            @RequestBody LlmParameterExtractionRequestDto req) {

        var model = modelService.getModelById(req.modelId(), getCurrentUserId());
        var document = documentService.getById(req.documentId());
        var params = parameterService.getParametersByModelId(req.modelId(), getCurrentUserId());

        LlmParameterExtractionResponseDto response =
                llmService.extractParameters(req, model, document, params);

        return ResponseEntity.ok(response);
    }

    /**
     * Чат с LLM для получения ответов по выбранной модели.
     *
     * @param req параметры диалога и идентификатор модели
     * @return ответ LLM на запрос пользователя
     */
    @PostMapping("/chat")
    public ResponseEntity<LlmChatResponseDto> chat(
            @RequestBody LlmChatRequestDto req) {

        var model = modelService.getModelById(req.modelId(), getCurrentUserId());

        LlmChatResponseDto response = llmService.chat(req, model);

        return ResponseEntity.ok(response);
    }
}
