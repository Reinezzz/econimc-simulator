package com.example.economicssimulatorserver.util;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Утилита для подстановки переменных в HTML-шаблоны.
 */
@Component
public class TemplateUtil {

    /**
     * Загружает шаблон и подставляет значения переменных вида {@code {{ключ}}}.
     *
     * @param templateName имя файла шаблона в classpath
     * @param vars         карта переменных и их значений
     * @return итоговый HTML с подставленными значениями
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
