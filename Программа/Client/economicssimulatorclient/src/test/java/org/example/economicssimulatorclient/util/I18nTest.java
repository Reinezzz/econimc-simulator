package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class I18nTest {

    @Test
    void testDefaultLocaleIsEnglish() {
        assertEquals(Locale.ENGLISH, I18n.getLocale());
    }

    @Test
    void setLocaleChangesLocale() {
        Locale ru = new Locale("ru");
        I18n.setLocale(ru);
        assertEquals(ru, I18n.getLocale());
    }

    @Test
    void tReturnsExclamationForUnknownKey() {
        String unknown = I18n.t("some_non_existing_key_12345");
        assertEquals("!some_non_existing_key_12345!", unknown);
    }
}
