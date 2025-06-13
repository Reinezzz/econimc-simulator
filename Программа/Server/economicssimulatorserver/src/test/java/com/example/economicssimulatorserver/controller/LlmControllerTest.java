package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LlmController.class)
class LlmControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean LlmService llmService;
    @MockBean EconomicModelService modelService;
    @MockBean DocumentService documentService;
    @MockBean ModelParameterService parameterService;
    @MockBean ModelCalculationService calculationService;
    @MockBean UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(10L);
        user.setUsername("test");
        when(userRepository.findByUsername(eq("test"))).thenReturn(Optional.of(user));
    }



    @Test
    @WithMockUser(username = "test")
    void extractParameters_success() throws Exception {
        LlmParameterExtractionRequestDto req = new LlmParameterExtractionRequestDto(1L, 2L);
        EconomicModelDto model = new EconomicModelDto(1L, "T", "Name", "desc", List.of(), List.of(), "d", "u", "f");
        DocumentDto doc = new DocumentDto(2L, 10L, "a", "b", null);
        List<ModelParameterDto> params = List.of(
                new ModelParameterDto(1L, 1L, "a", "N", "7", "a", "desc", 0)
        );
        LlmParameterExtractionResponseDto resp = new LlmParameterExtractionResponseDto(params);

        when(modelService.getModelById(eq(1L), eq(10L))).thenReturn(model);
        when(documentService.getById(2L)).thenReturn(doc);
        when(parameterService.getParametersByModelId(1L, 10L)).thenReturn(params);
        when(llmService.extractParameters(any(), eq(model), eq(doc), eq(params))).thenReturn(resp);

        mockMvc.perform(post("/api/llm/extract-parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parameters[0].paramName").value("a"));
    }

    @Test
    @WithMockUser(username = "test")
    void chat_success() throws Exception {
        LlmChatRequestDto req = new LlmChatRequestDto(1L, "", List.of(), null, null);
        EconomicModelDto model = new EconomicModelDto(1L, "T", "Name", "desc", List.of(), List.of(), "d", "u", "f");
        LlmChatResponseDto resp = new LlmChatResponseDto("answer");

        when(modelService.getModelById(eq(1L), eq(10L))).thenReturn(model);
        when(llmService.chat(any(), eq(model))).thenReturn(resp);

        mockMvc.perform(post("/api/llm/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assistantMessage").value("answer"));
    }
}
