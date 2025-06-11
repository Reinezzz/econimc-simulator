package com.example.economicssimulatorserver.util;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class TemplateUtil {

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
