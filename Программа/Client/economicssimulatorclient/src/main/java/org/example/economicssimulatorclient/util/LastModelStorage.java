package org.example.economicssimulatorclient.util;

import java.util.prefs.Preferences;

public class LastModelStorage {
    private static final String KEY = "last_model_id";
    private static final Preferences prefs = Preferences.userNodeForPackage(LastModelStorage.class);

    public static void saveLastModelId(long modelId) {
        prefs.putLong(KEY, modelId);
    }

    public static long loadLastModelId() {
        return prefs.getLong(KEY, -1);
    }
}
