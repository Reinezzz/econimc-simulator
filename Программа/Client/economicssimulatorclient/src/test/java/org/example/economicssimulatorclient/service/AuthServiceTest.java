package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = Mockito.spy(new AuthService());
    }

    @Test
    void testIsAuthenticatedInitiallyFalse() {
        assertFalse(authService.isAuthenticated());
    }

    @Test
    void testBearerHeaderFormat() throws Exception {
        // Установим accessToken руками через login
        var loginResp = new LoginResponse("mytoken", "Bearer");
        Mockito.doReturn(loginResp).when(authService).login(Mockito.any());
        authService.login(new LoginRequest("user", "pass"));
        assertEquals("Bearer mytoken", authService.bearerHeader());
    }

    @Test
    void testCancelRegistrationDoesNotThrow() {
        assertDoesNotThrow(() -> authService.cancelRegistration("test@email.com"));
    }

    @Test
    void testCancelPasswordResetDoesNotThrow() {
        assertDoesNotThrow(() -> authService.cancelPasswordReset("test@email.com"));
    }

    @Test
    void testErrorMessageExtract() {
        Exception ex = new Exception("{\"success\":false,\"message\":\"msg.test_error\"}");
        String msg = authService.extractErrorMessage(ex);
        assertEquals("msg.test_error", msg);
    }

    @Test
    void testErrorMessageExtractWithInvalidJson() {
        Exception ex = new Exception("not a json string");
        String msg = authService.extractErrorMessage(ex);
        assertEquals("not a json string", msg);
    }
}
