package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.SessionManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.*;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Spy
    ReportService service = new ReportService(URI.create("http://localhost/api/"));

    @Mock
    SessionManager sessionManager;

    @Mock
    AuthService authService;

    @Mock
    HttpClient httpClient;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        SessionManager.setInstance(sessionManager);

        // Подменяем singleton-AuthService
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, authService);

        // Прокидываем mock httpClient (если нужно подменять его для getBytes, ниже пример)
    }

    @AfterEach
    void cleanup() throws Exception {
        SessionManager.setInstance(new SessionManager());
        Field instanceField = AuthService.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void createReport_success() throws Exception {
        ReportCreateRequestDto req = new ReportCreateRequestDto(
                1L, "CAPM", "Отчёт", "ru", List.of(),
                null, List.of(), List.of(), "parsed"
        );
        ReportListItemDto expected = new ReportListItemDto(
                10L, "Отчёт", "CAPM", "/path", OffsetDateTime.now(), "ru"
        );
        doReturn(expected).when(service).post(
                any(), eq("/api/reports"), eq(req), eq(ReportListItemDto.class), eq(true), isNull()
        );

        ReportListItemDto actual = service.createReport(req);
        assertEquals(expected, actual);
    }

    @Test
    void getReports_success() throws Exception {
        ReportListItemDto item = new ReportListItemDto(
                12L, "Test", "CAPM", "/p", OffsetDateTime.now(), "ru"
        );
        doReturn(new ReportListItemDto[]{item}).when(service).get(
                any(), eq("/api/reports"), eq(ReportListItemDto[].class), eq(true), isNull()
        );

        List<ReportListItemDto> reports = service.getReports();
        assertEquals(1, reports.size());
        assertEquals(item, reports.get(0));
    }

    @Test
    void deleteReport_success() throws Exception {
        doNothing().when(service).delete(
                any(), eq("/api/reports/15"), eq(true), isNull()
        );
        assertDoesNotThrow(() -> service.deleteReport(15L));
        verify(service).delete(any(), eq("/api/reports/15"), eq(true), isNull());
    }

    @Test
    void downloadReport_success() throws Exception {
        byte[] expected = new byte[]{1, 2, 3, 4};
        doReturn(expected).when(service).getBytes(
                any(), eq("/api/reports/99/download"), eq(true), isNull()
        );

        byte[] actual = service.downloadReport(99L);
        assertArrayEquals(expected, actual);
    }

    @Test
    void getBytes_success() throws Exception {
        // Мокаем SessionManager и HttpClient
        when(sessionManager.getAccessToken()).thenReturn("access");
        HttpClient client = mock(HttpClient.class);
        HttpRequest[] capturedRequest = new HttpRequest[1];

        HttpResponse<byte[]> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new byte[]{42, 99});
        when(client.send(any(), any(HttpResponse.BodyHandler.class))).thenAnswer(invocation -> {
            capturedRequest[0] = invocation.getArgument(0);
            return mockResponse;
        });

        // Подменяем HttpClient в getBytes
        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class, CALLS_REAL_METHODS)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(client);

            byte[] result = service.getBytes(URI.create("http://localhost/api/"), "/api/reports/5/download", true, null);
            assertArrayEquals(new byte[]{42, 99}, result);
            assertTrue(capturedRequest[0].uri().toString().contains("/api/reports/5/download"));
        }
    }

    @Test
    void getBytes_401_refresh_success() throws Exception {
        // Первое обращение: 401, после refresh 200
        when(sessionManager.getAccessToken()).thenReturn("access", "newAccess");
        HttpClient client = mock(HttpClient.class);

        HttpResponse<byte[]> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);
        HttpResponse<byte[]> resp200 = mock(HttpResponse.class);
        when(resp200.statusCode()).thenReturn(200);
        when(resp200.body()).thenReturn(new byte[]{88, 77});

        when(client.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(resp401)
                .thenReturn(resp200);

        when(authService.refreshTokens()).thenReturn(true);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class, CALLS_REAL_METHODS)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(client);

            byte[] bytes = service.getBytes(URI.create("http://localhost/api/"), "/api/reports/17/download", true, null);
            assertArrayEquals(new byte[]{88, 77}, bytes);
        }
        verify(authService).refreshTokens();
    }

    @Test
    void getBytes_401_refresh_fail() throws Exception {
        when(sessionManager.getAccessToken()).thenReturn("access");
        HttpClient client = mock(HttpClient.class);
        HttpResponse<byte[]> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);
        when(client.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(resp401);

        when(authService.refreshTokens()).thenReturn(false);
        doNothing().when(authService).logout();

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class, CALLS_REAL_METHODS)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(client);

            assertThrows(IllegalArgumentException.class, () ->
                    service.getBytes(URI.create("http://localhost/api/"), "/api/reports/42/download", true, null)
            );
        }
        verify(authService).logout();
    }

    @Test
    void getBytes_non200_throws() throws Exception {
        when(sessionManager.getAccessToken()).thenReturn("access");
        HttpClient client = mock(HttpClient.class);
        HttpResponse<byte[]> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(404);
        when(client.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class, CALLS_REAL_METHODS)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(client);

            IOException ex = assertThrows(IOException.class, () ->
                    service.getBytes(URI.create("http://localhost/api/"), "/api/reports/77/download", true, null)
            );
            assertTrue(ex.getMessage().contains("HTTP code: 404"));
        }
    }
}
