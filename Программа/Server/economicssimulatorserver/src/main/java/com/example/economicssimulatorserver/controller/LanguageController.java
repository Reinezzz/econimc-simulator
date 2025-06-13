package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.config.LocaleHolder;
import com.example.economicssimulatorserver.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

/**
 * Контроллер выбора языка пользовательского интерфейса.
 */
@RestController
@RequestMapping("/api/lang")
public class LanguageController {

    /**
     * Устанавливает предпочитаемый язык клиента.
     *
     * @param body карта с ключом {@code lang} содержащим код языка
     * @return подтверждение успешного сохранения языка
     */
    @PostMapping
    public ApiResponse setLanguage(@RequestBody Map<String, String> body) {
        String lang = body.get("lang");
        if (lang != null) {
            LocaleHolder.setLocale(Locale.forLanguageTag(lang));
        }
        return new ApiResponse(true, "ok");
    }
}