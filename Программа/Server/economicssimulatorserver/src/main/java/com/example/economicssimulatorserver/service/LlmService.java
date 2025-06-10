package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${llm.ollama.host}")
    private String ollamaHost;

    @Value("${llm.ollama.model}")
    private String ollamaModel;

    @Value("${llm.ollama.timeout:30s}")
    private String ollamaTimeout;

    @Value("${llm.ollama.max-retries:3}")
    private int maxRetries;

    @Value("${llm.ollama.language.default:ru}")
    private String defaultLanguage;

    // --- Публичные методы ---

    /**
     * Извлечение параметров из PDF через LLM (mistral).
     * Возвращает список параметров (ModelParameterDto) с заполненными значениями.
     */
    public LlmParameterExtractionResponseDto extractParameters(LlmParameterExtractionRequestDto req,
                                                               EconomicModelDto model,
                                                               DocumentDto document,
                                                               List<ModelParameterDto> modelParameters) {
        int attempt = 0;
        Exception lastException = null;
        while (attempt < maxRetries) {
            try {
                String prompt = buildExtractionPrompt(model, document, modelParameters);
                String llmResponse = callOllama(prompt, ollamaModel);
                // Парсим json-ответ LLM в нужную структуру
                List<ModelParameterDto> updatedParams = parseExtractionResponse(llmResponse, modelParameters);
                return new LlmParameterExtractionResponseDto(updatedParams);
            } catch (Exception ex) {
                lastException = ex;
                log.warn("Failed to parse LLM response for extraction (attempt {}): {}", attempt + 1, ex.getMessage());
                attempt++;
            }
        }
        throw new LocalizedException("error.llm_invalid_response", maxRetries);
    }

    /**
     * Общение с LLM в чат-режиме (объяснение модели, параметров, анализ результата).
     */
    public LlmChatResponseDto chat(LlmChatRequestDto req, EconomicModelDto model) {
        int attempt = 0;
        Exception lastException = null;
        while (attempt < maxRetries) {
            try {
                String prompt = buildChatPrompt(req, model);
                String llmResponse = callOllama(prompt, ollamaModel);
                // Парсим только текст ответа, LLM должна вернуть текст
                String assistantMessage = parseChatResponse(llmResponse);
                return new LlmChatResponseDto(assistantMessage);
            } catch (Exception ex) {
                lastException = ex;
                log.warn("Failed to parse LLM response for chat (attempt {}): {}", attempt + 1, ex.getMessage());
                attempt++;
            }
        }
        throw new LocalizedException("error.llm_invalid_response", maxRetries);
    }

    // --- Внутренние методы ---

    /** Формируем prompt для extraction запроса */
    private String buildExtractionPrompt(EconomicModelDto model, DocumentDto document, List<ModelParameterDto> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("Твоя задача — найти значения параметров экономической модели из PDF-документа. ");
        sb.append("Модель: ").append(model.name()).append(".\n");
        sb.append("Описание модели: ").append(model.description()).append("\n");
        sb.append("Параметры модели:\n");
        for (ModelParameterDto p : params) {
            sb.append("- ").append(p.displayName()).append(" (").append(p.paramName()).append("): ")
                    .append(p.description()).append(" [Тип: ").append(p.paramType()).append("]\n");
        }
        sb.append("\nДокумент: ").append(document.name()).append("\n");
        sb.append("Извлеки значения всех параметров из документа (или придумай реалистичные значения, если не найдено!). ");
        sb.append("Ответь строго в формате JSON МАССИВА по шаблону:\n");
        sb.append("[{\"paramName\": \"имя параметра\", \"paramValue\": \"значение\"}, ...]\n");
        sb.append("Не добавляй пояснений или текста, только JSON.\n");
        return sb.toString();
    }

    /** Формируем prompt для chat запроса */
    private String buildChatPrompt(LlmChatRequestDto req, EconomicModelDto model) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ты ассистент для обучения экономическим моделям. ");
        sb.append("Отвечай кратко и только по теме модели. ").append("Модель: ").append(model.name()).append(".\n");
        sb.append("Описание модели: ").append(model.description()).append("\n");

        sb.append("Параметры и их значения:\n");
        for (ModelParameterDto p : req.parameters()) {
            sb.append("- ").append(p.displayName()).append(" (").append(p.paramName()).append("): ")
                    .append(p.paramValue()).append(" [Тип: ").append(p.paramType()).append("] — ").append(p.description()).append("\n");
        }

        if (req.visualizations() != null && !req.visualizations().isEmpty()) {
            sb.append("\nДанные визуализации (для анализа):\n");
            for (LlmVisualizationDto vis : req.visualizations()) {
                sb.append("- График: ").append(vis.chartTitle()).append(" (").append(vis.chartKey()).append(")\n");
                sb.append("  Данные: ").append(vis.chartData().toString()).append("\n");
            }
        }

        if (req.result() != null) {
            sb.append("\nРезультат расчёта модели:\n");
            sb.append(req.result().toString()).append("\n");
        }

        sb.append("Сообщение пользователя: ").append(req.userMessage()).append("\n");
        sb.append("Отвечай строго по теме модели и параметров, избегай разговоров не по теме экономики и этой модели.\n");
        sb.append("Язык ответа: ").append(defaultLanguage).append("\n");
        return sb.toString();
    }

    /**
     * Вызывает Ollama (mistral) через REST API, отправляет prompt, возвращает ответ как строку
     */
    private String callOllama(String prompt, String model) {
        String url = ollamaHost + "/api/generate";
        Map<String, Object> payload = Map.of(
                "model", model,
                "prompt", prompt,
                "options", Map.of(
                        "temperature", 0.3,
                        "timeout", ollamaTimeout
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Resource> response = restTemplate.exchange(
                url, HttpMethod.POST, request, Resource.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new LocalizedException("error.ollama_status", response.getStatusCode());
        }

        StringBuilder jsonBuilder = new StringBuilder();
        try (InputStream is = Objects.requireNonNull(response.getBody()).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Лог каждой строки


                // Парсим строку как JSON и достаем "response"
                if (line.trim().isEmpty()) continue;
                Map<String, Object> lineObj = objectMapper.readValue(line, Map.class);

                // Если есть поле response — добавляем к результату
                Object respPart = lineObj.get("response");
                if (respPart != null && !respPart.toString().isBlank()) {
                    jsonBuilder.append(respPart.toString());
                }
            }
        } catch (IOException | java.io.IOException e) {
            throw new LocalizedException("error.ollama_read");
        }

        String finalJson = jsonBuilder.toString().trim();
        if (finalJson.isEmpty()) {
            throw new LocalizedException("error.llm_empty_response");
        }
        System.out.println("OLLAMA >>> " + finalJson);
        return finalJson;
    }


    /**
     * Парсит JSON-массив от LLM для extraction (список параметров)
     */
    private List<ModelParameterDto> parseExtractionResponse(String llmJson, List<ModelParameterDto> originParams) {
        // Используем свой json utils или любой json парсер (например, Jackson)
        // Ниже пример с Jackson:
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, String>> arr = objectMapper.readValue(llmJson, List.class);
            List<ModelParameterDto> result = new ArrayList<>();
            for (ModelParameterDto orig : originParams) {
                String value = arr.stream()
                        .filter(x -> orig.paramName().equals(x.get("paramName")))
                        .map(x -> x.get("paramValue"))
                        .findFirst()
                        .orElse(orig.paramValue());
                // сохраняем все остальные поля без изменений, только значение подставляем новое
                result.add(new ModelParameterDto(
                        orig.id(), orig.modelId(), orig.paramName(), orig.paramType(),
                        value, orig.displayName(), orig.description(), orig.customOrder()
                ));
            }
            return result;
        } catch (Exception ex) {
            throw new LocalizedException("error.llm_parse_json", ex.getMessage());
        }
    }

    /**
     * Парсит ответ LLM для чата (текстовое сообщение)
     */
    private String parseChatResponse(String llmRaw) {
        // В простейшем случае это просто текст.
        // Если понадобится расширить протокол - обработать как json
        return llmRaw.trim();
    }
}
