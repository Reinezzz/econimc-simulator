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
 * Кэширует FXML и переключает root у Scene — без мелькания окна.
 * Управляет загрузкой и переключением сцен приложения.
 */
public final class SceneManager {

    /**
     * Главный Stage приложения.
     */
    private static Stage primary;
    /**
     * Кэш для загруженных FXML-деревьев.
     */
    private static final Map<String, Parent> cache = new HashMap<>();
    /**
     * Главная сцена.
     */
    private static final Scene scene = new Scene(new javafx.scene.Group());
    /**
     * Префикс пути к FXML-файлам.
     */
    public static final String ROOT = "/org/example/economicssimulatorclient/";

    /**
     * Приватный конструктор для запрета создания экземпляров.
     */
    private SceneManager() {}

    /**
     * Инициализация менеджера сцен (один раз из MainApp).
     * @param stage основной Stage приложения
     */
    public static void init(Stage stage) {
        primary = stage;
        primary.setScene(scene);
    }

    /**
     * Переключиться на заданный FXML (из ресурсов).
     * @param fxml имя FXML-файла
     */
    public static void switchTo(String fxml) {
        try {
            Parent root = cache.computeIfAbsent(fxml, SceneManager::load);
            scene.setRoot(root);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Загружает и локализует FXML-файл.
     * @param fxml имя файла
     * @return {@link Parent} корневой элемент сцены
     */
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
