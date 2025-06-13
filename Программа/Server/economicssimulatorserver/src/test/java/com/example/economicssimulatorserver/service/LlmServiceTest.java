package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LlmServiceTest {

    @Mock MessageSource messageSource;
    @Mock RestTemplate restTemplate;
    @InjectMocks LlmService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // set private fields
        service.ollamaHost = "http://localhost:11434";
        service.ollamaModel = "test-model";
        service.maxRetries = 1;
        service.ollamaTimeout = "30s";
        service.defaultLanguage = "ru";
    }


    @Test
    void extractParameters_badResponse_throws() {
        LlmParameterExtractionRequestDto req = mock(LlmParameterExtractionRequestDto.class);
        EconomicModelDto model = mock(EconomicModelDto.class);
        DocumentDto doc = mock(DocumentDto.class);
        List<ModelParameterDto> params = List.of(new ModelParameterDto(1L, 1L, "alpha", "NUMBER", "0", "A", "desc", 0));

        for (String key : List.of(
                "prompt.extract.task", "prompt.extract.model", "prompt.extract.description",
                "prompt.extract.parameters_header", "prompt.extract.param_item",
                "prompt.doc", "prompt.extract.document", "prompt.extract.explain",
                "prompt.extract.format", "prompt.extract.template", "prompt.extract.no_explanation"
        )) {
            when(messageSource.getMessage(eq(key), any(), any(Locale.class))).thenReturn(key);
        }
        when(doc.name()).thenReturn("Документ");
        when(model.name()).thenReturn("Модель");
        when(model.description()).thenReturn("Описание");

        String badJson = "{\"response\":\"badjson\"}\n";
        ByteArrayResource resource = new ByteArrayResource(badJson.getBytes());
        ResponseEntity<Resource> entity = new ResponseEntity<>(resource, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Resource.class)))
                .thenReturn(entity);

        assertThatThrownBy(() -> service.extractParameters(req, model, doc, params))
                .isInstanceOf(LocalizedException.class);
    }
}
