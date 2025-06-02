package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.ApiResponse;
import org.example.economicssimulatorclient.util.JsonUtil;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    @Mock
    HttpClient httpClient;

    BaseService baseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseService = new BaseService() {
            { this.httpClient = BaseServiceTest.this.httpClient; }
        };
    }

    @Test
    void postSuccess() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        ApiResponse resp = new ApiResponse(true, "ok");
        String json = JsonUtil.toJson(resp);

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        ApiResponse result = baseService.post(baseUri, "endpoint", resp, ApiResponse.class, false, null);
        assertEquals(resp, result);
    }

    @Test
    void postThrowsOnBadStatus() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("bad request");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        Exception ex = assertThrows(RuntimeException.class, () ->
                baseService.post(baseUri, "endpoint", new ApiResponse(true, "ok"), ApiResponse.class, false, null));
        assertTrue(ex.getMessage().contains("HTTP 400"));
    }

    @Test
    void postThrowsOnSerializationError() {
        URI baseUri = URI.create("http://localhost/api/");
        // Jackson выбросит ошибку сериализации для Object.class
        Object unserializable = new Object();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                baseService.post(baseUri, "endpoint", unserializable, ApiResponse.class, false, null));
        assertTrue(ex.getMessage().contains("Ошибка сериализации"));
    }

    @Test
    void getSuccess() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        ApiResponse resp = new ApiResponse(true, "ok");
        String json = JsonUtil.toJson(resp);

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        ApiResponse result = baseService.get(baseUri, "endpoint", ApiResponse.class, false, null);
        assertEquals(resp, result);
    }

    @Test
    void getThrowsOnBadStatus() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("not found");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        Exception ex = assertThrows(RuntimeException.class, () ->
                baseService.get(baseUri, "endpoint", ApiResponse.class, false, null));
        assertTrue(ex.getMessage().contains("HTTP 404"));
    }

    @Test
    void putSuccess() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        ApiResponse resp = new ApiResponse(true, "ok");
        String json = JsonUtil.toJson(resp);

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        ApiResponse result = baseService.put(baseUri, "endpoint", resp, ApiResponse.class, false, null);
        assertEquals(resp, result);
    }

    @Test
    void putThrowsOnBadStatus() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("error");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        Exception ex = assertThrows(RuntimeException.class, () ->
                baseService.put(baseUri, "endpoint", new ApiResponse(true, "ok"), ApiResponse.class, false, null));
        assertTrue(ex.getMessage().contains("HTTP 500"));
    }

    @Test
    void putThrowsOnSerializationError() {
        URI baseUri = URI.create("http://localhost/api/");
        Object unserializable = new Object();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                baseService.put(baseUri, "endpoint", unserializable, ApiResponse.class, false, null));
        assertTrue(ex.getMessage().contains("Ошибка сериализации"));
    }

    @Test
    void deleteSuccess() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(204);
        when(httpResponse.body()).thenReturn("");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        assertDoesNotThrow(() ->
                baseService.delete(baseUri, "endpoint", false, null)
        );
    }

    @Test
    void deleteThrowsOnBadStatus() throws Exception {
        URI baseUri = URI.create("http://localhost/api/");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(403);
        when(httpResponse.body()).thenReturn("forbidden");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        Exception ex = assertThrows(RuntimeException.class, () ->
                baseService.delete(baseUri, "endpoint", false, null));
        assertTrue(ex.getMessage().contains("HTTP 403"));
    }
}
