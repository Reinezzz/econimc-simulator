package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.LlmChatRequestDto;
import org.example.economicssimulatorclient.dto.LlmChatResponseDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionRequestDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionResponseDto;

import java.io.IOException;
import java.net.URI;

public class LlmService extends MainService {

    private static LlmService instance;

    private static final String BASE_PATH = "/api/llm";

    private LlmService() {}

    public static synchronized LlmService getInstance() {
        if (instance == null) {
            instance = new LlmService();
        }
        return instance;
    }

    /**
     * Чат с LLM: для экрана просмотра модели и экрана результата.
     * @param baseUri корень API (обычно SessionManager.getInstance().getBaseUri())
     * @param request  DTO с id модели, сообщением, параметрами, визуализациями и т.д.
     * @return ответ от LLM (LlmChatResponseDto)
     */
    public LlmChatResponseDto chat(URI baseUri, LlmChatRequestDto request)
            throws IOException, InterruptedException {
        return post(
                baseUri,
                BASE_PATH + "/chat",
                request,
                LlmChatResponseDto.class,
                true,  // всегда авторизован
                null   // токен автоматом подхватится из MainService
        );
    }

    /**
     * Извлечение параметров из документа через LLM (на экране выбора документа).
     * @param baseUri корень API
     * @param request DTO с modelId и documentId
     * @return список параметров с новыми значениями (LlmParameterExtractionResponseDto)
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
