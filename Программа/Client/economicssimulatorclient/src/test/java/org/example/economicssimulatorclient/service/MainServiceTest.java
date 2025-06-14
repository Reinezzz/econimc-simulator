package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainServiceTest {

    @Spy
    MainService service;

    @Mock
    SessionManager sessionManager;

    @Mock
    AuthService authService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Подменяем singleton-SessionManager
        SessionManager.setInstance(sessionManager);
        when(sessionManager.getAccessToken()).thenReturn("access");
        // Подменяем singleton-AuthService
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, authService);
    }

    @AfterEach
    void cleanup() throws Exception {
        // Сброс singleton-ов если понадобится в других тестах
        SessionManager.setInstance(new SessionManager());
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    // ===== POST =====

    @Test
    void post_delegatesToSuper() throws Exception {
        Object expected = new Object();
        doReturn(expected).when((BaseService) service)
                .post(any(), eq("ep"), any(), eq(Object.class), anyBoolean(), any());
        Object result = service.post(new URI("http://localhost/"), "ep", "body", Object.class, true, "access");
        assertSame(expected, result);
    }

    @Test
    void post_retriesOn401_andRefreshSucceeds() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .doReturn("ok")
                .when((BaseService) service)
                .post(any(), eq("ep"), any(), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(true);
        when(sessionManager.getAccessToken()).thenReturn("access", "accessNew");

        String result = service.post(new URI("http://localhost/"), "ep", "body", String.class, true, "access");
        assertEquals("ok", result);
        verify(authService, never()).logout();
    }

    @Test
    void post_retriesOn401_andRefreshFails() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .when((BaseService) service)
                .post(any(), eq("ep"), any(), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(false);
        doNothing().when(authService).logout();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.post(new URI("http://localhost/"), "ep", "body", String.class, true, "access"));
        assertTrue(ex.getMessage().contains("session_expired"));
        verify(authService).logout();
    }

    @Test
    void post_propagatesNon401Errors() throws Exception {
        doThrow(new RuntimeException("Some error"))
                .when((BaseService) service)
                .post(any(), eq("ep"), any(), eq(Object.class), anyBoolean(), any());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.post(new URI("http://localhost/"), "ep", "body", Object.class, true, "access"));
        assertEquals("Some error", ex.getMessage());
    }

    // ===== GET =====

    @Test
    void get_delegatesToSuper() throws Exception {
        Object expected = new Object();
        doReturn(expected).when((BaseService) service)
                .get(any(), eq("ep"), eq(Object.class), anyBoolean(), any());
        Object result = service.get(new URI("http://localhost/"), "ep", Object.class, true, "access");
        assertSame(expected, result);
    }

    @Test
    void get_retriesOn401_andRefreshSucceeds() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .doReturn("ok")
                .when((BaseService) service)
                .get(any(), eq("ep"), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(true);
        when(sessionManager.getAccessToken()).thenReturn("access", "accessNew");

        String result = service.get(new URI("http://localhost/"), "ep", String.class, true, "access");
        assertEquals("ok", result);
        verify(authService, never()).logout();
    }

    @Test
    void get_retriesOn401_andRefreshFails() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .when((BaseService) service)
                .get(any(), eq("ep"), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(false);
        doNothing().when(authService).logout();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.get(new URI("http://localhost/"), "ep", String.class, true, "access"));
        assertTrue(ex.getMessage().contains("session_expired"));
        verify(authService).logout();
    }

    @Test
    void get_propagatesNon401Errors() throws Exception {
        doThrow(new RuntimeException("Some error"))
                .when((BaseService) service)
                .get(any(), eq("ep"), eq(Object.class), anyBoolean(), any());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.get(new URI("http://localhost/"), "ep", Object.class, true, "access"));
        assertEquals("Some error", ex.getMessage());
    }

    // ===== PUT =====

    @Test
    void put_delegatesToSuper() throws Exception {
        Object expected = new Object();
        doReturn(expected).when((BaseService) service)
                .put(any(), eq("ep"), any(), eq(Object.class), anyBoolean(), any());
        Object result = service.put(new URI("http://localhost/"), "ep", "body", Object.class, true, "access");
        assertSame(expected, result);
    }

    @Test
    void put_retriesOn401_andRefreshSucceeds() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .doReturn("ok")
                .when((BaseService) service)
                .put(any(), eq("ep"), any(), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(true);
        when(sessionManager.getAccessToken()).thenReturn("access", "accessNew");

        String result = service.put(new URI("http://localhost/"), "ep", "body", String.class, true, "access");
        assertEquals("ok", result);
        verify(authService, never()).logout();
    }

    @Test
    void put_retriesOn401_andRefreshFails() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .when((BaseService) service)
                .put(any(), eq("ep"), any(), eq(String.class), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(false);
        doNothing().when(authService).logout();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.put(new URI("http://localhost/"), "ep", "body", String.class, true, "access"));
        assertTrue(ex.getMessage().contains("session_expired"));
        verify(authService).logout();
    }

    @Test
    void put_propagatesNon401Errors() throws Exception {
        doThrow(new RuntimeException("Some error"))
                .when((BaseService) service)
                .put(any(), eq("ep"), any(), eq(Object.class), anyBoolean(), any());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.put(new URI("http://localhost/"), "ep", "body", Object.class, true, "access"));
        assertEquals("Some error", ex.getMessage());
    }

    // ===== DELETE =====

    @Test
    void delete_delegatesToSuper() throws Exception {
        doNothing().when((BaseService) service)
                .delete(any(), eq("ep"), anyBoolean(), any());
        assertDoesNotThrow(() ->
                service.delete(new URI("http://localhost/"), "ep", true, "access"));
    }

    @Test
    void delete_retriesOn401_andRefreshSucceeds() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .doNothing()
                .when((BaseService) service)
                .delete(any(), eq("ep"), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(true);
        when(sessionManager.getAccessToken()).thenReturn("access", "accessNew");

        assertDoesNotThrow(() ->
                service.delete(new URI("http://localhost/"), "ep", true, "access"));
        verify(authService, never()).logout();
    }

    @Test
    void delete_retriesOn401_andRefreshFails() throws Exception {
        doThrow(new RuntimeException("HTTP 401: unauthorized"))
                .when((BaseService) service)
                .delete(any(), eq("ep"), anyBoolean(), any());
        when(authService.refreshTokens()).thenReturn(false);
        doNothing().when(authService).logout();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.delete(new URI("http://localhost/"), "ep", true, "access"));
        assertTrue(ex.getMessage().contains("session_expired"));
        verify(authService).logout();
    }

    @Test
    void delete_propagatesNon401Errors() throws Exception {
        doThrow(new RuntimeException("Some error"))
                .when((BaseService) service)
                .delete(any(), eq("ep"), anyBoolean(), any());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.delete(new URI("http://localhost/"), "ep", true, "access"));
        assertEquals("Some error", ex.getMessage());
    }
}
