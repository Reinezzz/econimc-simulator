package com.example.economicssimulatorserver.config;

import java.util.Locale;

/**
 * Хранилище текущей локали приложения.
 * Используется для передачи информации о выбранном языке.
 */
public class LocaleHolder {
    private static Locale locale = Locale.forLanguageTag("ru");

    /**
     * Возвращает текущую локаль.
     *
     * @return установленная локаль
     */
    public static synchronized Locale getLocale() {
        return locale;
    }

    /**
     * Устанавливает локаль приложения.
     *
     * @param l новая локаль
     */
    public static synchronized void setLocale(Locale l) {
        locale = l;
    }
}