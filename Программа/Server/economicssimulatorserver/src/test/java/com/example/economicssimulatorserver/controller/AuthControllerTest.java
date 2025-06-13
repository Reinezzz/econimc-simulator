package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private CacheManager cacheManager;

    private static final RegistrationRequest REG_REQ =
            new RegistrationRequest("testuser", "test@email.com", "pass123");
    private static final VerificationRequest VERIFY_REQ =
            new VerificationRequest("test@email.com", "123456");
    private static final LoginRequest LOGIN_REQ =
            new LoginRequest("testuser", "pass123");
    private static final PasswordResetRequest RESET_REQ =
            new PasswordResetRequest("test@email.com");
    private static final PasswordResetConfirm RESET_CONFIRM_REQ =
            new PasswordResetConfirm("test@email.com", "123456", "newpass");
    private static final RefreshTokenRequest REFRESH_REQ =
            new RefreshTokenRequest("some-refresh-token");
    private static final LogoutRequest LOGOUT_REQ =
            new LogoutRequest("some-refresh-token");

    @Test
    @DisplayName("POST /auth/register - successful registration")
    void testRegister() throws Exception {
        Mockito.when(authService.register(any(RegistrationRequest.class)))
                .thenReturn(new ApiResponse(true, "msg.verification_code_sent"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@email.com","password":"pass123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("msg.verification_code_sent"));
    }

    @Test
    @DisplayName("POST /auth/verify-email - successful verification")
    void testVerifyEmail() throws Exception {
        Mockito.when(authService.verifyEmail(any(VerificationRequest.class)))
                .thenReturn(new ApiResponse(true, "msg.email_confirmed"));

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@email.com","code":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("msg.email_confirmed"));
    }

    @Test
    @DisplayName("POST /auth/login - successful login")
    void testLogin() throws Exception {
        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("access", "refresh", "Bearer"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"testuser","password":"pass123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth/password-reset - initiate password reset")
    void testPasswordReset() throws Exception {
        Mockito.when(authService.initiatePasswordReset(any(PasswordResetRequest.class)))
                .thenReturn(new ApiResponse(true, "msg.password_rest_code_sent"));

        mockMvc.perform(post("/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@email.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /auth/password-reset/confirm - confirm password reset")
    void testPasswordResetConfirm() throws Exception {
        Mockito.when(authService.confirmPasswordReset(any(PasswordResetConfirm.class)))
                .thenReturn(new ApiResponse(true, "msg.password_updated"));

        mockMvc.perform(post("/auth/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@email.com","code":"123456","newPassword":"newpass"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /auth/cancel-registration")
    void testCancelRegistration() throws Exception {
        mockMvc.perform(post("/auth/cancel-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@email.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("msg.registration_canceled"));
    }

    @Test
    @DisplayName("POST /auth/cancel-password-reset")
    void testCancelPasswordReset() throws Exception {
        Mockito.doNothing().when(authService).cancelPasswordReset(anyString());

        mockMvc.perform(post("/auth/cancel-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@email.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("msg.password_reset_canceled"));
    }

    @Test
    @DisplayName("POST /auth/refresh - successful refresh")
    void testRefresh() throws Exception {
        Mockito.when(authService.refreshTokens(any(RefreshTokenRequest.class)))
                .thenReturn(new RefreshTokenResponse("access", "refresh"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"some-refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    @DisplayName("POST /auth/logout")
    void testLogout() throws Exception {
        Mockito.doNothing().when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"some-refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("msg.logged_out"));
    }



}
