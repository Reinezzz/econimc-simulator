package org.example.economicssimulatorclient.util;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Класс для управления локализацией приложения.
 * Хранит текущую локаль и загружает ResourceBundle соответствующей страны.
 * Метод {@link #t(String)} возвращает перевод по ключу или ключ в восклицательных знаках, если перевод не найден.
 */
public class I18n {

    @Getter
    private static Locale locale = Locale.ENGLISH;

    public static ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

    /**
     * Изменяет текущую локаль и перезагружает файл сообщений.
     *
     * @param newLocale новая локаль интерфейса
     */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Возвращает перевод строки по ключу из ResourceBundle.
     * Если ключ не найден, оборачивает его в восклицательные знаки.
     *
     * @param key ключ сообщения
     * @return локализованная строка либо "!key!"
     */
    public static String t(String key) {
        if (bundle == null) setLocale(locale);
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return "!" + key + "!";
    }
}
