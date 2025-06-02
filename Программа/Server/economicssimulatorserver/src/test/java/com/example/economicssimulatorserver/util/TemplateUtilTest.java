package com.example.economicssimulatorserver.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TemplateUtilTest {

    @Test
    void testRenderWithVars() {
        TemplateUtil util = new TemplateUtil();
        String result = util.render("testTemplate.html", Map.of("name", "Alex", "code", "1234"));
        assertThat(result).contains("Hi, Alex! Your code: 1234.");
    }
}
