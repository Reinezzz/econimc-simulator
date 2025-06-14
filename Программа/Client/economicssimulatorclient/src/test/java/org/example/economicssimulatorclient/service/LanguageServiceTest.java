package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageServiceTest {

    @Spy
    LanguageService service;

    @Mock
    SessionManager sessionManager;

    @Mock
    AuthService authService;

    final URI baseUri = URI.create("http://localhost/api/");

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        SessionManager.setInstance(sessionManager);

        // Подменяем singleton-AuthService
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, authService);

        // Спай всегда возвращает наш URI
        Field uriField = LanguageService.class.getDeclaredField("baseUri");
        uriField.setAccessible(true);
        uriField.set(service, baseUri);
    }

    @AfterEach
    void cleanup() throws Exception {
        SessionManager.setInstance(new SessionManager());
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void updateLanguage_success() throws Exception {
        doReturn(new Object()).when(service)
                .post(eq(baseUri), eq("lang"), eq(Map.of("lang", "ru")), eq(Object.class), eq(true), isNull());
        assertDoesNotThrow(() -> service.updateLanguage("ru"));
    }


    @Test
    void updateLanguage_throwsNon401() throws Exception {
        doThrow(new RuntimeException("HTTP 500: oops"))
                .when(service)
                .post(eq(baseUri), eq("lang"), eq(Map.of("lang", "ru")), eq(Object.class), eq(true), isNull());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateLanguage("ru"));
        assertEquals("HTTP 500: oops", ex.getMessage());
    }
}
