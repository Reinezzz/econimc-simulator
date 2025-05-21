package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class I18nTest {

    private static Locale originalLocale;

    @BeforeAll
    static void saveLocale() {
        originalLocale = I18n.getLocale();
    }

    @AfterAll
    static void restoreLocale() {
        I18n.setLocale(originalLocale);
    }

    @Test
    void testSetLocaleAndTranslate() {
        I18n.setLocale(Locale.ENGLISH);
        String hello = I18n.t("common.hello");
        assertNotNull(hello);
        assertNotEquals("!common.hello!", hello, "Должен вернуть перевод для существующего ключа");

        String notExist = I18n.t("no_such_key_test_123");
        assertEquals("!no_such_key_test_123!", notExist, "Неизвестный ключ должен вернуть !key!");
    }

    @Test
    void testLocaleChangeAffectsBundle() {
        I18n.setLocale(new Locale("ru"));
        String hello = I18n.t("common.hello");
        assertNotNull(hello);
    }
}
