package com.example.economicssimulatorserver.util;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Утилита для рендеринга HTML-шаблонов по принципу простой подстановки плейсхолдеров.
 * Заменяет в шаблоне все {{placeholder}} на соответствующее значение из vars.
 */
@Component
public class TemplateUtil {

    /**
     * Загружает HTML-шаблон из classpath и подставляет значения переменных.
     *
     * @param templateName имя файла шаблона (например, verification_email.html)
     * @param vars         карта плейсхолдеров и значений
     * @return финальный HTML с подставленными значениями
     */
    @SneakyThrows
    public String render(String templateName, Map<String, String> vars) {
        var resource = new ClassPathResource(templateName);
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        for (var entry : vars.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }
}
