package org.example.economicssimulatorclient.util;

import java.util.prefs.Preferences;

/**
 * Хранилище идентификатора последней открытой модели.
 * Значение сохраняется в {@link java.util.prefs.Preferences}, что позволяет восстанавливать состояние между запусками.
 */
public class LastModelStorage {
    private static final String KEY = "last_model_id";
    private static final Preferences prefs = Preferences.userNodeForPackage(LastModelStorage.class);

    /**
     * Сохраняет идентификатор последней открытой модели в пользовательских настройках.
     *
     * @param modelId id модели
     */
    public static void saveLastModelId(long modelId) {
        prefs.putLong(KEY, modelId);
    }

    /**
     * Загружает ранее сохранённый идентификатор модели.
     *
     * @return id последней модели или -1, если данных нет
     */
    public static long loadLastModelId() {
        return prefs.getLong(KEY, -1);
    }
}
