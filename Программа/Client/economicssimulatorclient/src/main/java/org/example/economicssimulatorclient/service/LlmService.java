package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.LlmChatRequestDto;
import org.example.economicssimulatorclient.dto.LlmChatResponseDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionRequestDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionResponseDto;

import java.io.IOException;
import java.net.URI;

/**
 * Сервис для взаимодействия с языковой моделью.
 */
public class LlmService extends MainService {

    private static LlmService instance;

    private static final String BASE_PATH = "/api/llm";

    LlmService() {
    }

    /**
     * @return лениво создаваемый единственный экземпляр
     */
    public static synchronized LlmService getInstance() {
        if (instance == null) {
            instance = new LlmService();
        }
        return instance;
    }

    /**
     * Отправляет сообщение в LLM.
     */
    public LlmChatResponseDto chat(URI baseUri, LlmChatRequestDto request)
            throws IOException, InterruptedException {
        return post(
                baseUri,
                BASE_PATH + "/chat",
                request,
                LlmChatResponseDto.class,
                true,
                null
        );
    }

    /**
     * Извлекает параметры модели из текста с помощью LLM.
     */
    public LlmParameterExtractionResponseDto extractParameters(URI baseUri, LlmParameterExtractionRequestDto request)
            throws IOException, InterruptedException {
        return post(
                baseUri,
                BASE_PATH + "/extract-parameters",
                request,
                LlmParameterExtractionResponseDto.class,
                true,
                null
        );
    }
}
