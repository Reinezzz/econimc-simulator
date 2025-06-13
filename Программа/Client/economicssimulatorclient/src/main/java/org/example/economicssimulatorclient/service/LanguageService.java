package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Сервис для смены языка интерфейса на сервере.
 */
public class LanguageService extends MainService {
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + "/api/");

    /**
     * Сохраняет выбранный язык пользователя на сервере.
     */
    public void updateLanguage(String lang) throws IOException, InterruptedException {
        post(baseUri, "lang", Map.of("lang", lang), Object.class, true, null);
    }
}