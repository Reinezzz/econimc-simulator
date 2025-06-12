package com.example.economicssimulatorserver.config;

import java.util.Locale;

public class LocaleHolder {
    private static Locale locale = Locale.forLanguageTag("ru");

    public static synchronized Locale getLocale() {
        return locale;
    }

    public static synchronized void setLocale(Locale l) {
        locale = l;
    }
}