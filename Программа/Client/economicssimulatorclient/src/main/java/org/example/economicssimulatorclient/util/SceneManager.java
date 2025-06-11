package org.example.economicssimulatorclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public final class SceneManager {

    private static Stage primary;
    private static final Map<String, Parent> cache = new HashMap<>();
    private static final Scene scene = new Scene(new javafx.scene.Group());
    public static final String ROOT = "/org/example/economicssimulatorclient/";

    private static final Deque<String> history = new ArrayDeque<>();
    private static String currentFxml = null;
    private static SceneManager INSTANCE = new SceneManager();

    private SceneManager() {
    }

    public static void init(Stage stage) {
        primary = stage;
        primary.setScene(scene);
    }

    public static void switchToWithoutContorller(String fxml) {
        try {
            Parent root = cache.computeIfAbsent(fxml, SceneManager::load);
            scene.setRoot(root);
            currentFxml = fxml;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

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

    public static <T> void switchTo(String fxml, java.util.function.Consumer<T> controllerConsumer) {
        try {
            if (currentFxml != null)
                history.push(currentFxml);
            currentFxml = fxml;
            I18n.setLocale(Locale.forLanguageTag("ru"));
            Locale locale = I18n.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(ROOT + fxml));
            loader.setResources(bundle);
            Parent root = loader.load();
            T controller = loader.getController();
            controllerConsumer.accept(controller);
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setInstance(SceneManager mockSceneManager) {
        INSTANCE = mockSceneManager;
    }
}
