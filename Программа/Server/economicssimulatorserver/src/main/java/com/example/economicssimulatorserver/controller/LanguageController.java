package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.config.LocaleHolder;
import com.example.economicssimulatorserver.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/lang")
public class LanguageController {

    @PostMapping
    public ApiResponse setLanguage(@RequestBody Map<String, String> body) {
        String lang = body.get("lang");
        if (lang != null) {
            LocaleHolder.setLocale(Locale.forLanguageTag(lang));
        }
        return new ApiResponse(true, "ok");
    }
}