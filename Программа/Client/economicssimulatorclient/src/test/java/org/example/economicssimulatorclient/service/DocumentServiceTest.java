package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.util.I18n;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Spy
    DocumentService service = Mockito.spy(new DocumentService());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserDocuments_returnsList() throws Exception {
        DocumentDto doc = new DocumentDto(1L, 2L, "doc.pdf", "user/2/doc.pdf", LocalDateTime.now());
        DocumentDto[] docs = new DocumentDto[] { doc };
        doReturn(docs).when(service)
                .get(any(), eq(""), eq(DocumentDto[].class), anyBoolean(), any());

        List<DocumentDto> result = service.getUserDocuments();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("doc.pdf", result.get(0).name());
    }

    @Test
    void getUserDocuments_throwsOnError() throws Exception {
        doThrow(new IOException("fail")).when(service)
                .get(any(), eq(""), eq(DocumentDto[].class), anyBoolean(), any());

        assertThrows(IOException.class, () -> service.getUserDocuments());
    }

    @Test
    void uploadDocument_successPdf() throws Exception {
        File file = File.createTempFile("doc", ".pdf");
        file.deleteOnExit();
        DocumentDto doc = new DocumentDto(2L, 4L, "doc.pdf", "user/4/doc.pdf", LocalDateTime.now());

        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();
        HttpResponse<String> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn("{\"id\":2,\"userId\":4,\"name\":\"doc.pdf\",\"path\":\"user/4/doc.pdf\",\"uploadedAt\":\"2024-06-13T15:00:00\"}");
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResp);

        DocumentDto result = service.uploadDocument(file);
        assertNotNull(result);
        assertEquals("doc.pdf", result.name());
    }

    @Test
    void uploadDocument_throwsIfNullOrMissing() {
        assertThrows(IllegalArgumentException.class, () -> service.uploadDocument(null));
        File f = new File("missing.pdf");
        assertThrows(IllegalArgumentException.class, () -> service.uploadDocument(f));
    }

    @Test
    void uploadDocument_throwsIfNotPdf() throws Exception {
        File txt = File.createTempFile("doc", ".txt");
        txt.deleteOnExit();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.uploadDocument(txt));
        assertTrue(ex.getMessage().contains(I18n.t("error.only_pdf")));
    }

    @Test
    void uploadDocument_401_triggersRefreshAndRetries() throws Exception {
        File file = File.createTempFile("doc", ".pdf");
        file.deleteOnExit();

        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<String> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);

        HttpResponse<String> resp200 = mock(HttpResponse.class);
        when(resp200.statusCode()).thenReturn(200);
        when(resp200.body()).thenReturn("{\"id\":1,\"userId\":1,\"name\":\"doc.pdf\",\"path\":\"user/1/doc.pdf\",\"uploadedAt\":\"2024-06-13T15:00:00\"}");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(resp401)
                .thenReturn(resp200);

        doReturn(true).when(service).tryRefreshToken();

        DocumentDto doc = service.uploadDocument(file);
        assertNotNull(doc);
        assertEquals("doc.pdf", doc.name());
        verify(service, times(1)).tryRefreshToken();
    }

    @Test
    void uploadDocument_401_refreshFails_throws() throws Exception {
        File file = File.createTempFile("doc", ".pdf");
        file.deleteOnExit();

        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<String> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(resp401);

        doReturn(false).when(service).tryRefreshToken();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.uploadDocument(file));
        assertTrue(ex.getMessage().contains(I18n.t("error.session_expired")));
    }

    @Test
    void uploadDocument_throwsOnOtherHttpError() throws Exception {
        File file = File.createTempFile("doc", ".pdf");
        file.deleteOnExit();

        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(403);
        when(resp.body()).thenReturn("fail");

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(resp);

        Exception ex = assertThrows(RuntimeException.class, () -> service.uploadDocument(file));
        assertTrue(ex.getMessage().contains(I18n.t("error.upload_failed")));
    }

    @Test
    void downloadDocument_success() throws Exception {
        long docId = 123L;
        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<InputStream> mockResp = mock(HttpResponse.class);
        when(mockResp.statusCode()).thenReturn(200);
        when(mockResp.body()).thenReturn(new ByteArrayInputStream("PDF".getBytes(StandardCharsets.UTF_8)));

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofInputStream()))).thenReturn(mockResp);

        InputStream is = service.downloadDocument(docId);
        assertNotNull(is);
        assertEquals("PDF", new String(is.readAllBytes(), StandardCharsets.UTF_8));
    }

    @Test
    void downloadDocument_401_triggersRefreshAndRetries() throws Exception {
        long docId = 321L;
        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<InputStream> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);

        HttpResponse<InputStream> resp200 = mock(HttpResponse.class);
        when(resp200.statusCode()).thenReturn(200);
        when(resp200.body()).thenReturn(new ByteArrayInputStream("PDF2".getBytes(StandardCharsets.UTF_8)));

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofInputStream())))
                .thenReturn(resp401)
                .thenReturn(resp200);

        doReturn(true).when(service).tryRefreshToken();

        InputStream is = service.downloadDocument(docId);
        assertNotNull(is);
        assertEquals("PDF2", new String(is.readAllBytes(), StandardCharsets.UTF_8));
        verify(service, times(1)).tryRefreshToken();
    }

    @Test
    void downloadDocument_401_refreshFails_throws() throws Exception {
        long docId = 777L;
        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<InputStream> resp401 = mock(HttpResponse.class);
        when(resp401.statusCode()).thenReturn(401);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofInputStream()))).thenReturn(resp401);

        doReturn(false).when(service).tryRefreshToken();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.downloadDocument(docId));
        assertTrue(ex.getMessage().contains(I18n.t("error.session_expired")));
    }

    @Test
    void downloadDocument_throwsOnOtherHttpError() throws Exception {
        long docId = 808L;
        doReturn("token").when(service).getToken();
        HttpClient mockClient = mock(HttpClient.class);
        doReturn(mockClient).when(service).getHttpClient();

        HttpResponse<InputStream> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(403);

        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofInputStream()))).thenReturn(resp);

        Exception ex = assertThrows(RuntimeException.class, () -> service.downloadDocument(docId));
        assertTrue(ex.getMessage().contains(I18n.t("error.download_failed")));
    }

    @Test
    void deleteDocument_callsDeleteParent() throws Exception {
        doNothing().when(service).delete(any(), anyString(), anyBoolean(), any());
        service.deleteDocument(5L);
        verify(service).delete(any(), eq("5"), eq(true), any());
    }
}
