package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;

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

    public LlmChatResponseDto chat(LlmChatRequestDto req, EconomicModelDto model) {
        int attempt = 0;
        Exception lastException = null;
        while (attempt < maxRetries) {
            try {
                String prompt = buildChatPrompt(req, model);
                String llmResponse = callOllama(prompt, ollamaModel);
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

    private String buildExtractionPrompt(EconomicModelDto model, DocumentDto document, List<ModelParameterDto> params) {
        Locale locale = Locale.forLanguageTag(defaultLanguage);
        StringBuilder sb = new StringBuilder();
        sb.append(messageSource.getMessage("prompt.extract.task", null, locale)).append(' ');
        sb.append(messageSource.getMessage("prompt.extract.model", new Object[]{model.name()}, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.extract.description", new Object[]{model.description()}, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.extract.parameters_header", null, locale)).append("\n");
        for (ModelParameterDto p : params) {
            sb.append(messageSource.getMessage("prompt.extract.param_item",
                    new Object[]{p.displayName(), p.paramName(), p.description(), p.paramType()}, locale)).append("\n");
        }
        sb.append("\nДокумент: ").append(document.name()).append("\n");
        sb.append("\n").append(messageSource.getMessage("prompt.extract.document", new Object[]{document.name()}, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.extract.explain", null, locale)).append(' ');
        sb.append(messageSource.getMessage("prompt.extract.format", null, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.extract.template", null, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.extract.no_explanation", null, locale)).append("\n");
        return sb.toString();
    }

    private String buildChatPrompt(LlmChatRequestDto req, EconomicModelDto model) {
        Locale locale = Locale.forLanguageTag(defaultLanguage);
        StringBuilder sb = new StringBuilder();
        sb.append(messageSource.getMessage("prompt.chat.intro", null, locale)).append(' ');
        sb.append(messageSource.getMessage("prompt.chat.topic", new Object[]{model.name()}, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.chat.description", new Object[]{model.description()}, locale)).append("\n");

        sb.append(messageSource.getMessage("prompt.chat.parameters", null, locale)).append("\n");
        for (ModelParameterDto p : req.parameters()) {
            sb.append(messageSource.getMessage("prompt.chat.param_item",
                    new Object[]{p.displayName(), p.paramName(), p.paramValue(), p.paramType(), p.description()}, locale)).append("\n");
        }

        if (req.visualizations() != null && !req.visualizations().isEmpty()) {
            sb.append("\n").append(messageSource.getMessage("prompt.chat.visual_header", null, locale)).append("\n");
            for (LlmVisualizationDto vis : req.visualizations()) {
                sb.append(messageSource.getMessage("prompt.chat.visual_chart",
                        new Object[]{vis.chartTitle(), vis.chartKey()}, locale)).append("\n");
                sb.append(messageSource.getMessage("prompt.chat.visual_data",
                        new Object[]{vis.chartData().toString()}, locale)).append("\n");
            }
        }

        if (req.result() != null) {
            sb.append("\n").append(messageSource.getMessage("prompt.chat.result_header", null, locale)).append("\n");
            sb.append(req.result().toString()).append("\n");
        }

        sb.append(messageSource.getMessage("prompt.chat.user_message", new Object[]{req.userMessage()}, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.chat.keep_topic", null, locale)).append("\n");
        sb.append(messageSource.getMessage("prompt.chat.language", new Object[]{defaultLanguage}, locale)).append("\n");
        return sb.toString();
    }

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
                if (line.trim().isEmpty()) continue;
                Map<String, Object> lineObj = objectMapper.readValue(line, Map.class);

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
        return finalJson;
    }


    private List<ModelParameterDto> parseExtractionResponse(String llmJson, List<ModelParameterDto> originParams) {
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

    private String parseChatResponse(String llmRaw) {
        return llmRaw.trim();
    }
}
