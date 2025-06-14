package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LlmServiceTest {

    @Spy
    LlmService service = Mockito.spy(new LlmService());

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void chat_returnsAssistantMessage() throws Exception {
        URI uri = URI.create("http://localhost");
        LlmChatRequestDto req = new LlmChatRequestDto(
                1L, "Hello",
                List.of(),
                List.of(new LlmVisualizationDto("chart", "ChartTitle", Map.of("a", 5))),
                new ModelResultDto(10L, 1L, "res", "{}", "2024-06-13T15:00:00")
        );
        LlmChatResponseDto expected = new LlmChatResponseDto("Assistant reply");

        doReturn(expected).when(service).post(
                eq(uri),
                eq("/api/llm/chat"),
                eq(req),
                eq(LlmChatResponseDto.class),
                eq(true),
                isNull()
        );

        LlmChatResponseDto result = service.chat(uri, req);
        assertNotNull(result);
        assertEquals("Assistant reply", result.assistantMessage());
    }

    @Test
    void extractParameters_returnsParameters() throws Exception {
        URI uri = URI.create("http://localhost");
        LlmParameterExtractionRequestDto req = new LlmParameterExtractionRequestDto(1L, 2L);
        ModelParameterDto param = new ModelParameterDto(
                1L, 1L, "p", "double", "10", "Параметр", "desc", 1
        );
        LlmParameterExtractionResponseDto expected =
                new LlmParameterExtractionResponseDto(List.of(param));

        doReturn(expected).when(service).post(
                eq(uri),
                eq("/api/llm/extract-parameters"),
                eq(req),
                eq(LlmParameterExtractionResponseDto.class),
                eq(true),
                isNull()
        );

        LlmParameterExtractionResponseDto result = service.extractParameters(uri, req);
        assertNotNull(result);
        assertEquals(1, result.parameters().size());
        assertEquals("p", result.parameters().get(0).paramName());
    }

    @Test
    void singleton_instance() {
        // Проверяем singleton-семантику
        LlmService inst1 = LlmService.getInstance();
        LlmService inst2 = LlmService.getInstance();
        assertSame(inst1, inst2);
    }
}
