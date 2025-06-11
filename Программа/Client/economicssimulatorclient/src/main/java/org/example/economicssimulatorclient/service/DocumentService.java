package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class DocumentService extends MainService {

    private static final String BASE_API = "/api/documents";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + BASE_API + "/");

    public List<DocumentDto> getUserDocuments() throws IOException, InterruptedException {
        String endpoint = "";
        DocumentDto[] arr = get(baseUri, endpoint, DocumentDto[].class, true, null);
        return Arrays.asList(arr);
    }

    public DocumentDto uploadDocument(File pdfFile) throws IOException, InterruptedException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException(I18n.t("error.file_not_found"));
        }
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException(I18n.t("error.only_pdf"));
        }

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8), true);

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(pdfFile.getName()).append("\"\r\n");
        writer.append("Content-Type: application/pdf\r\n\r\n");
        writer.flush();
        Files.copy(pdfFile.toPath(), bos);
        bos.flush();
        writer.append("\r\n");
        writer.flush();

        writer.append("--").append(boundary).append("--\r\n");
        writer.close();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri.resolve("upload"))
                .header("Authorization", "Bearer " + getToken())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(bos.toByteArray()))
                .build();

        HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 401) {
            if (tryRefreshToken()) {
                return uploadDocument(pdfFile);
            } else {
                throw new IllegalArgumentException(I18n.t("error.session_expired"));
            }
        }
        if (response.statusCode() >= 400) {
            throw new RuntimeException(I18n.t("error.upload_failed") + response.body());
        }
        return JsonUtil.fromJson(response.body(), DocumentDto.class);
    }

    public InputStream downloadDocument(long documentId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri.resolve(documentId + "/download"))
                .header("Authorization", "Bearer " + getToken())
                .GET()
                .build();
        HttpResponse<InputStream> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 401) {
            if (tryRefreshToken()) {
                return downloadDocument(documentId);
            } else {
                throw new IllegalArgumentException(I18n.t("error.session_expired"));
            }
        }
        if (response.statusCode() >= 400) {
            throw new RuntimeException(I18n.t("error.download_failed"));
        }
        return response.body();
    }

    public void deleteDocument(long documentId) throws IOException, InterruptedException {
        String endpoint = documentId + "";
        super.delete(baseUri, endpoint, true, null);
    }

    private String getToken() {
        return org.example.economicssimulatorclient.util.SessionManager.getInstance().getAccessToken();
    }

    private HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }

    private boolean tryRefreshToken() throws IOException, InterruptedException {
        return org.example.economicssimulatorclient.service.AuthService.getInstance().refreshTokens();
    }
}
