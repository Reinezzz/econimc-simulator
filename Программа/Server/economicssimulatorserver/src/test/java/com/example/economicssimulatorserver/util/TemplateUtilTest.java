package com.example.economicssimulatorserver.util;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateUtilTest {

    @Test
    void render_shouldReplacePlaceholders() throws Exception {
        // Создаём временный шаблон
        String templateName = "test_template.html";
        String templateContent = "<div>Hello, {{name}}! Your code: {{code}}</div>";

        // Записываем шаблон в classpath (src/test/resources/)
        try (OutputStream out = new FileOutputStream("src/test/resources/" + templateName)) {
            out.write(templateContent.getBytes(StandardCharsets.UTF_8));
        }

        TemplateUtil util = new TemplateUtil();
        String html = util.render(templateName, Map.of("name", "Alice", "code", "123456"));

        assertEquals("<div>Hello, Alice! Your code: 123456</div>", html);
    }

    @Test
    void render_shouldLeaveUnknownPlaceholdersUntouched() throws Exception {
        String templateName = "test_template2.html";
        String templateContent = "Test: {{known}} {{unknown}}";

        try (OutputStream out = new FileOutputStream("src/test/resources/" + templateName)) {
            out.write(templateContent.getBytes(StandardCharsets.UTF_8));
        }

        TemplateUtil util = new TemplateUtil();
        String html = util.render(templateName, Map.of("known", "YES"));

        // unknown останется как есть
        assertEquals("Test: YES {{unknown}}", html);
    }

    @Test
    void render_shouldThrowForMissingTemplate() {
        TemplateUtil util = new TemplateUtil();
        assertThrows(Exception.class, () ->
                util.render("no_such_template.html", Map.of("x", "y")));
    }
}
