package org.example.economicssimulatorclient.util;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    @Getter
    private static Locale locale = Locale.ENGLISH;

    public static ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String t(String key) {
        if (bundle == null) setLocale(locale);
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return "!" + key + "!";
    }
}
