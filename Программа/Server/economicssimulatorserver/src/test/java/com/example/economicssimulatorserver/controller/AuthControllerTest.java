package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private AuthService authService;
    @Mock private CacheManager cacheManager;
    @Mock private Cache cache;

    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // ----- REGISTRATION -----

    @Test
    void register_ShouldReturnCreated() throws Exception {
        RegistrationRequest req = new RegistrationRequest("user", "mail@ex.com", "pass");
        ApiResponse response = new ApiResponse(true, "ok");
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"email\":\"mail@ex.com\",\"password\":\"pass\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void verifyEmail_ShouldReturnSuccess() throws Exception {
        VerificationRequest req = new VerificationRequest("mail@ex.com", "123456");
        ApiResponse response = new ApiResponse(true, "ok");
        when(authService.verifyEmail(any())).thenReturn(response);

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"mail@ex.com\",\"code\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ----- LOGIN -----

    @Test
    void login_ShouldReturnToken() throws Exception {
        LoginRequest req = new LoginRequest("user", "pass");
        LoginResponse response = new LoginResponse("access", "Bearer");
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    // ----- PASSWORD RESET -----

    @Test
    void passwordReset_ShouldReturnSuccess() throws Exception {
        PasswordResetRequest req = new PasswordResetRequest("mail@ex.com");
        ApiResponse response = new ApiResponse(true, "sent");
        when(authService.initiatePasswordReset(any())).thenReturn(response);

        mockMvc.perform(post("/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"mail@ex.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void passwordResetConfirm_ShouldReturnSuccess() throws Exception {
        PasswordResetConfirm req = new PasswordResetConfirm("mail@ex.com", "123456", "newpw");
        ApiResponse response = new ApiResponse(true, "ok");
        when(authService.confirmPasswordReset(any())).thenReturn(response);

        mockMvc.perform(post("/auth/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"mail@ex.com\",\"code\":\"123456\",\"newPassword\":\"newpw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ----- CANCEL REGISTRATION -----

    @Test
    void cancelRegistration_ShouldEvictCacheAndReturnSuccess() throws Exception {
        mockMvc.perform(post("/auth/cancel-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"mail@ex.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(cache, atLeastOnce()).evict("mail@ex.com");
    }

    @Test
    void cancelPasswordReset_ShouldCallServiceAndReturnSuccess() throws Exception {
        doNothing().when(authService).cancelPasswordReset("mail@ex.com");
        mockMvc.perform(post("/auth/cancel-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"mail@ex.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(authService, atLeastOnce()).cancelPasswordReset("mail@ex.com");
    }
}
