package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.JsonUtil;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    HttpClient mockClient;

    static class TestBaseService extends BaseService {}

    @Spy
    TestBaseService service = Mockito.spy(new TestBaseService());

    @BeforeEach
    void setup() throws Exception {
        mockClient = mock(HttpClient.class); // вот здесь объявление
        MockitoAnnotations.openMocks(this);
        // ...
        Field clientField = BaseService.class.getDeclaredField("httpClient");
        clientField.setAccessible(true);
        clientField.set(service, mockClient);
    }

    @Test
    void post_successfulRequest_returnsParsedResponse() throws Exception {
        URI uri = new URI("http://test/api/");
        String endpoint = "endpoint";
        DummyDto reqObj = new DummyDto("x");
        DummyDto respObj = new DummyDto("y");
        String respJson = JsonUtil.toJson(respObj);


        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn(respJson);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        DummyDto result = service.post(uri, endpoint, reqObj, DummyDto.class, true, "token");
        assertNotNull(result);
        assertEquals("y", result.value());
    }

    @Test
    void post_throwsOnSerializationError() {
        URI uri = URI.create("http://test/");
        Object broken = new Object() { }; // will not serialize
        assertThrows(RuntimeException.class, () ->
                service.post(uri, "e", broken, DummyDto.class, false, null)
        );
    }

    @Test
    void post_throwsOnHttpError() throws Exception {
        URI uri = new URI("http://test/api/");
        String endpoint = "endpoint";
        DummyDto reqObj = new DummyDto("z");

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(400);
        when(mockResp.body()).thenReturn("Bad request!");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.post(uri, endpoint, reqObj, DummyDto.class, true, "tok")
        );
        assertTrue(ex.getMessage().contains("400"));
    }

    @Test
    void post_setsAuthorizationHeader_whenAuthEnabled() throws Exception {
        URI uri = new URI("http://test/api/");
        DummyDto reqObj = new DummyDto("z");

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn(JsonUtil.toJson(reqObj));

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        when(mockClient.send(captor.capture(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        service.post(uri, "endpoint", reqObj, DummyDto.class, true, "tok123");

        HttpRequest reqSent = captor.getValue();
        assertEquals("Bearer tok123", reqSent.headers().firstValue("Authorization").orElse(""));
        assertEquals("en", reqSent.headers().firstValue("Accept-Language").orElse(""));
    }

    @Test
    void get_successfulRequest_returnsParsedResponse() throws Exception {
        URI uri = new URI("http://test/api/");
        DummyDto respObj = new DummyDto("out");
        String respJson = JsonUtil.toJson(respObj);

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn(respJson);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        DummyDto result = service.get(uri, "endpoint", DummyDto.class, true, "token");
        assertEquals("out", result.value());
    }

    @Test
    void get_throwsOnHttpError() throws Exception {
        URI uri = new URI("http://test/api/");

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(404);
        when(mockResp.body()).thenReturn("fail");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.get(uri, "notfound", DummyDto.class, false, null)
        );
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    void put_successfulRequest_returnsParsedResponse() throws Exception {
        URI uri = new URI("http://test/api/");
        DummyDto reqObj = new DummyDto("edit");
        DummyDto respObj = new DummyDto("afterEdit");
        String respJson = JsonUtil.toJson(respObj);

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn(respJson);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        DummyDto result = service.put(uri, "endpoint", reqObj, DummyDto.class, true, "token");
        assertEquals("afterEdit", result.value());
    }

    @Test
    void put_throwsOnHttpError() throws Exception {
        URI uri = new URI("http://test/api/");
        DummyDto reqObj = new DummyDto("edit");

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(403);
        when(mockResp.body()).thenReturn("fail");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.put(uri, "endpoint", reqObj, DummyDto.class, true, "token")
        );
        assertTrue(ex.getMessage().contains("403"));
    }

    @Test
    void delete_successfulRequest() throws Exception {
        URI uri = new URI("http://test/api/");

        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        assertDoesNotThrow(() -> service.delete(uri, "endpoint", true, "token"));
    }

    @Test
    void delete_throwsOnHttpError() throws Exception {
        URI uri = new URI("http://test/api/");
        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(500);
        when(mockResp.body()).thenReturn("fail");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.delete(uri, "endpoint", true, "token")
        );
        assertTrue(ex.getMessage().contains("500"));
    }

    // Dummy DTO for (de)serialization tests
    public record DummyDto(String value) {}
}
