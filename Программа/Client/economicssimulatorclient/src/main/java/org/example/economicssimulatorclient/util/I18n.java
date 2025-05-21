package org.example.economicssimulatorclient.util;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Универсальный менеджер локализации для всего приложения.
 * Позволяет динамически менять язык и получать переводы по ключу.
 */
public class I18n {
    /**
     * Текущая локаль приложения.
     */
    @Getter
    private static Locale locale = Locale.ENGLISH;

    /**
     * Текущий {@link ResourceBundle} с переводами.
     */
    public static ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

    /**
     * Установить локаль (например, Locale.ENGLISH или new Locale("ru")).
     * @param newLocale новая локаль
     */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Перевести по ключу. Если ключ не найден — вернуть !key!.
     * @param key ключ локализации
     * @return строка перевода или !key! если не найдено
     */
    public static String t(String key) {
        if (bundle == null) setLocale(locale);
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return "!" + key + "!";
    }
}
