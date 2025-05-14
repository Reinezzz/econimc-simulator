package org.example.economicssimulatorclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Кэширует FXML и переключает root у единственного Scene —
 * благодаря этому окно не перерисовывается целиком, «мелькания» нет.
 */
public final class SceneManager {

    private static Stage primary;
    private static final Map<String, Parent> cache = new HashMap<>();
    private static final Scene scene = new Scene(new javafx.scene.Group());

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

    /** Показать модальное окно (исп. для ввода кода) */
    public static String showInputDialog(String title, String header, String prompt) {
        javafx.scene.control.TextInputDialog dlg = new javafx.scene.control.TextInputDialog();
        dlg.initOwner(primary);
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.setTitle(title);
        dlg.setHeaderText(header);
        dlg.setContentText(prompt);
        return dlg.showAndWait().orElse(null);
    }

    /* ---------- private ---------- */
    private static Parent load(String fxml) {
        try {
            return new FXMLLoader(SceneManager.class.getResource("/" + fxml)).load();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + fxml, e);
        }
    }
}
