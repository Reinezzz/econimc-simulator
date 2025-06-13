package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.EconomicModelService;
import com.example.economicssimulatorserver.service.ModelParameterService;
import com.example.economicssimulatorserver.service.ModelCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EconomicModelController.class)
class EconomicModelControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean EconomicModelService economicModelService;
    @MockBean ModelParameterService modelParameterService;
    @MockBean ModelCalculationService modelCalculationService;
    @MockBean UserRepository userRepository;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setupUser() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User() {{ setId(2L); }}));
    }

    @Test
    @WithMockUser(username = "user")
    void getAllModels_success() throws Exception {
        when(economicModelService.getAllModels(2L)).thenReturn(List.of(
                new EconomicModelDto(1L, "T", "Name", "desc", List.of(), List.of(), "d", "u", "f")
        ));
        mockMvc.perform(get("/api/models/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Name"));
    }

    @Test
    @WithMockUser(username = "user")
    void getUserParameters_success() throws Exception {
        when(modelParameterService.getParametersByModelId(1L, 2L)).thenReturn(List.of(
                new ModelParameterDto(1L, 1L, "a", "N", "7", "a", "desc", 0)
        ));
        mockMvc.perform(get("/api/models/1/parameters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paramName").value("a"));
    }

    @Test
    @WithMockUser(username = "user")
    void calculate_success() throws Exception {
        CalculationRequestDto requestDto = new CalculationRequestDto(1L, "type", List.of());
        CalculationResponseDto responseDto = new CalculationResponseDto(null, null);
        when(modelCalculationService.calculate(any(), eq(2L))).thenReturn(responseDto);

        mockMvc.perform(post("/api/models/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
