package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Spy
    AuthService authService = Mockito.spy(AuthService.getInstance());

    @Mock
    SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SessionManager.setInstance(sessionManager);
    }

    @Test
    void registerReturnsApiResponse() throws Exception {
        RegistrationRequest req = new RegistrationRequest("user", "e@e.com", "pass123");
        ApiResponse expected = new ApiResponse(true, "OK");

        doReturn(expected).when(authService)
                .post(any(), eq("register"), eq(req), eq(ApiResponse.class), anyBoolean(), isNull());

        ApiResponse actual = authService.register(req);
        assertEquals(expected, actual);
    }

    @Test
    void verifyEmailReturnsApiResponse() throws Exception {
        VerificationRequest req = new VerificationRequest("e@e.com", "1234");
        ApiResponse expected = new ApiResponse(true, "verified");

        doReturn(expected).when(authService)
                .post(any(), eq("verify-email"), eq(req), eq(ApiResponse.class), anyBoolean(), isNull());

        ApiResponse actual = authService.verifyEmail(req);
        assertEquals(expected, actual);
    }

    @Test
    void loginSavesTokensAndReturnsLoginResponse() throws Exception {
        LoginRequest req = new LoginRequest("login", "pass");
        LoginResponse resp = new LoginResponse("access", "refresh", "Bearer");

        doReturn(resp).when(authService)
                .post(any(), eq("login"), eq(req), eq(LoginResponse.class), anyBoolean(), isNull());

        doNothing().when(sessionManager).saveTokens(anyString(), anyString());
        doNothing().when(sessionManager).resetJustLoggedOut();

        LoginResponse actual = authService.login(req);
        assertEquals(resp, actual);
        verify(sessionManager).saveTokens("access", "refresh");
        verify(sessionManager).resetJustLoggedOut();
    }

    @Test
    void resetPasswordRequestReturnsApiResponse() throws Exception {
        PasswordResetRequest req = new PasswordResetRequest("mail@mail.com");
        ApiResponse resp = new ApiResponse(true, "reset sent");

        doReturn(resp).when(authService)
                .post(any(), eq("password-reset"), eq(req), eq(ApiResponse.class), anyBoolean(), isNull());

        ApiResponse actual = authService.resetPasswordRequest(req);
        assertEquals(resp, actual);
    }

    @Test
    void resetPasswordConfirmReturnsApiResponse() throws Exception {
        PasswordResetConfirm req = new PasswordResetConfirm("mail@mail.com", "c", "n");
        ApiResponse resp = new ApiResponse(true, "done");

        doReturn(resp).when(authService)
                .post(any(), eq("password-reset/confirm"), eq(req), eq(ApiResponse.class), anyBoolean(), isNull());

        ApiResponse actual = authService.resetPasswordConfirm(req);
        assertEquals(resp, actual);
    }

    @Test
    void refreshTokensSavesNewTokensAndReturnsTrue() throws Exception {
        when(sessionManager.getRefreshToken()).thenReturn("refresh");
        RefreshTokenResponse resp = new RefreshTokenResponse("newAccess", "newRefresh");
        doReturn(resp).when(authService)
                .post(any(), eq("refresh"), any(), eq(RefreshTokenResponse.class), anyBoolean(), isNull());

        doNothing().when(sessionManager).saveTokens(anyString(), anyString());

        boolean refreshed = authService.refreshTokens();
        assertTrue(refreshed);
        verify(sessionManager).saveTokens("newAccess", "newRefresh");
    }

    @Test
    void refreshTokensReturnsFalseIfNoRefreshToken() throws Exception {
        when(sessionManager.getRefreshToken()).thenReturn(null);
        assertFalse(authService.refreshTokens());
    }

    @Test
    void refreshTokensLogoutOnServerError() throws Exception {
        when(sessionManager.getRefreshToken()).thenReturn("refresh");
        doThrow(new RuntimeException("HTTP 400: error")).when(authService)
                .post(any(), eq("refresh"), any(), eq(RefreshTokenResponse.class), anyBoolean(), isNull());

        doNothing().when(sessionManager).logout();

        boolean refreshed = authService.refreshTokens();
        assertFalse(refreshed);
        verify(sessionManager).logout();
    }

    @Test
    void extractErrorMessageFromJson() {
        Exception ex = new Exception("{\"success\":false,\"message\":\"error text\"}");
        String msg = authService.extractErrorMessage(ex);
        assertEquals("error text", msg);
    }

    @Test
    void extractErrorMessageFallback() {
        Exception ex = new Exception("just a plain error");
        String msg = authService.extractErrorMessage(ex);
        assertEquals("just a plain error", msg);
    }
}
