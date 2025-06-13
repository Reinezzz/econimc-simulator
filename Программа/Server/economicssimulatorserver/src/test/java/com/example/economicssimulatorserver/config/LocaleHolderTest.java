package com.example.economicssimulatorserver.config;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class LocaleHolderTest {

    @Test
    void setAndGetLocale() {
        LocaleHolder.setLocale(Locale.ENGLISH);
        assertThat(LocaleHolder.getLocale()).isEqualTo(Locale.ENGLISH);

        LocaleHolder.setLocale(Locale.forLanguageTag("ru"));
        assertThat(LocaleHolder.getLocale()).isEqualTo(Locale.forLanguageTag("ru"));
    }
}
