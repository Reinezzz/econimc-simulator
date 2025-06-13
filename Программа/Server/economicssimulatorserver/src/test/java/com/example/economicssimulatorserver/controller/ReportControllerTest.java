package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.ReportService;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ReportService reportService;
    @MockBean UserRepository userRepository;

    @BeforeEach
    void setupUser() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User() {{ setId(5L); setUsername("user"); }}));
    }

    @Test
    @WithMockUser(username = "user")
    void getReportsForUser_success() throws Exception {
        List<ReportListItemDto> list = List.of(
                new ReportListItemDto(1L, "n", "m", "p", OffsetDateTime.now(), "ru")
        );
        when(reportService.getReportsForUser(5L)).thenReturn(list);

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("n"));
    }

    @Test
    @WithMockUser(username = "user")
    void createReport_success() throws Exception {
        ReportCreateRequestDto dto = new ReportCreateRequestDto(1L, "model", "n", "ru", List.of(), null, List.of(), List.of(), "");
        ReportListItemDto resp = new ReportListItemDto(2L, "n", "m", "p", OffsetDateTime.now(), "ru");
        when(reportService.createReport(anyLong(), anyString(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }
}
