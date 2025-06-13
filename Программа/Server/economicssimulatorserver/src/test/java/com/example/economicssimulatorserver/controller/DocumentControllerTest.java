package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.DocumentDto;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean DocumentService documentService;
    @MockBean UserRepository userRepository;

    @BeforeEach
    void mockUser() {
        // username=test, id=1
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User() {{ setId(1L); }}));
    }


    @Test
    @WithMockUser(username = "test")
    void getUserDocuments_success() throws Exception {
        when(documentService.getUserDocuments(1L)).thenReturn(List.of(
                new DocumentDto(1L, 1L, "a.pdf", "a", LocalDateTime.now()))
        );

        mockMvc.perform(get("/api/documents/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("a.pdf"));
    }

    @Test
    @WithMockUser(username = "test")
    void downloadDocument_notFound() throws Exception {
        when(documentService.getUserDocuments(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/documents/55/download"))
                .andExpect(status().is4xxClientError()); // <-- исправлено
    }

}
