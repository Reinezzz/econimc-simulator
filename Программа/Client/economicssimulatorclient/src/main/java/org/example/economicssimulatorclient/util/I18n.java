package org.example.economicssimulatorclient.util;

import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    @Setter
    private static Locale locale = Locale.getDefault(); // Можно явно задать ru/en

    public static String t(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
