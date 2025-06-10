package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.ReportCreateRequestDto;
import org.example.economicssimulatorclient.dto.ReportListItemDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ReportService extends MainService {

    private final URI baseUri;

    public ReportService(URI baseUri) {
        this.baseUri = baseUri;
    }

    public ReportListItemDto createReport(ReportCreateRequestDto dto) throws IOException, InterruptedException {
        return post(
                baseUri,
                "/api/reports",
                dto,
                ReportListItemDto.class,
                true,
                null
        );
    }

    public List<ReportListItemDto> getReports() throws IOException, InterruptedException {
        ReportListItemDto[] arr = get(
                baseUri,
                "/api/reports",
                ReportListItemDto[].class,
                true,
                null
        );
        return Arrays.asList(arr);
    }

    public byte[] downloadReport(long reportId) throws IOException, InterruptedException {
        return getBytes(
                baseUri,
                "/api/reports/" + reportId + "/download",
                true,
                null
        );
    }

    public void deleteReport(long reportId) throws IOException, InterruptedException {
        delete(
                baseUri,
                "/api/reports/" + reportId,
                true,
                null
        );
    }

    protected byte[] getBytes(
            URI baseUri,
            String endpoint,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {
        String token = org.example.economicssimulatorclient.util.SessionManager.getInstance().getAccessToken();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 401) {
            if (org.example.economicssimulatorclient.service.AuthService.getInstance().refreshTokens()) {
                token = org.example.economicssimulatorclient.util.SessionManager.getInstance().getAccessToken();
                request = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            } else {
                org.example.economicssimulatorclient.service.AuthService.getInstance().logout();
                throw new IllegalArgumentException("Сессия истекла, войдите заново");
            }
        }
        if (response.statusCode() != 200) {
            throw new IOException("Download failed, HTTP code: " + response.statusCode());
        }
        return response.body();
    }
}
