package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class DocumentService extends MainService {

    private static final String BASE_API = "/api/documents";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + BASE_API + "/");

    // Получить список документов пользователя
    public List<DocumentDto> getUserDocuments() throws IOException, InterruptedException {
        String endpoint = "";
        DocumentDto[] arr = get(baseUri, endpoint, DocumentDto[].class, true, null);
        return Arrays.asList(arr);
    }

    // Загрузить PDF-документ
    public DocumentDto uploadDocument(File pdfFile) throws IOException, InterruptedException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("Файл не найден.");
        }
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Можно загружать только PDF-файлы.");
        }

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"), true);

        // Файл
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(pdfFile.getName()).append("\"\r\n");
        writer.append("Content-Type: application/pdf\r\n\r\n");
        writer.flush();
        Files.copy(pdfFile.toPath(), bos);
        bos.flush();
        writer.append("\r\n");
        writer.flush();


        // Конец
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
                return uploadDocument(pdfFile); // retry
            } else {
                throw new IllegalArgumentException("Сессия истекла, войдите заново");
            }
        }
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Ошибка загрузки файла: " + response.body());
        }
        return JsonUtil.fromJson(response.body(), DocumentDto.class);
    }

    // Скачать документ (PDF)
    public InputStream downloadDocument(long documentId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri.resolve(documentId + "/download"))
                .header("Authorization", "Bearer " + getToken())
                .GET()
                .build();
        HttpResponse<InputStream> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 401) {
            if (tryRefreshToken()) {
                return downloadDocument(documentId); // retry
            } else {
                throw new IllegalArgumentException("Сессия истекла, войдите заново");
            }
        }
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Ошибка скачивания файла");
        }
        return response.body();
    }

    // Удалить документ
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
