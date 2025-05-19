package org.example.economicssimulatorclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Кэширует FXML и переключает root у единственного Scene —
 * благодаря этому окно не перерисовывается целиком, «мелькания» нет.
 */
public final class SceneManager {

    private static Stage primary;
    private static final Map<String, Parent> cache = new HashMap<>();
    private static final Scene scene = new Scene(new javafx.scene.Group());
    public static final String ROOT = "/org/example/economicssimulatorclient/";

    private SceneManager() {}

    /** Вызывается один раз из MainApp */
    public static void init(Stage stage) { primary = stage; primary.setScene(scene); }

    /** Переключиться на fxml (из resources) */
    public static void switchTo(String fxml) {
        try {
            Parent root = cache.computeIfAbsent(fxml, SceneManager::load);
            scene.setRoot(root);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    /* ---------- private ---------- */
    private static Parent load(String fxml) {
        try {
            I18n.setLocale(Locale.forLanguageTag("ru"));
            Locale locale = I18n.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(ROOT + fxml));
            loader.setResources(bundle);
            return loader.load();

        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + fxml, e);
        }
    }
}
