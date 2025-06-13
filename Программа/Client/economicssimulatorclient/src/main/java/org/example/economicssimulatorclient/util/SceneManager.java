package org.example.economicssimulatorclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Управляет переключением сцен в JavaFX приложении.
 * Кэширует загруженные FXML, ведёт историю переходов и учитывает текущую локаль при загрузке.
 * Метод {@link #switchTo} даёт возможность настроить контроллер перед отображением сцены.
 */
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

    /**
     * Инициализирует менеджер сцен и сохраняет главную Stage.
     *
     * @param stage основное окно приложения
     */
    public static void init(Stage stage) {
        primary = stage;
        primary.setScene(scene);
    }

    /**
     * Переключает сцену без возможности настройки контроллера.
     *
     * @param fxml путь к FXML-файлу относительно {@link #ROOT}
     */
    public static void switchToWithoutContorller(String fxml) {
        try {
            Parent root = cache.computeIfAbsent(fxml, SceneManager::load);
            scene.setRoot(root);
            currentFxml = fxml;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Загружает FXML с учётом текущей локали и кэширует результат.
     *
     * @param fxml имя FXML-файла
     * @return загруженный корневой узел сцены
     */
    private static Parent load(String fxml) {
        try {
            Locale locale = I18n.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(ROOT + fxml));
            loader.setResources(bundle);
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + fxml, e);
        }
    }

    /**
     * Переключает сцену и передаёт контроллер потребителю для дополнительной настройки.
     *
     * @param fxml путь к FXML-файлу
     * @param controllerConsumer действие над контроллером
     * @param <T>  тип контроллера
     */
    public static <T> void switchTo(String fxml, java.util.function.Consumer<T> controllerConsumer) {
        try {
            if (currentFxml != null)
                history.push(currentFxml);
            currentFxml = fxml;
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

    /**
     * Перезагружает текущую сцену из файла, сбрасывая кэш.
     */
    public static void reloadCurrent() {
        if (currentFxml != null) {
            cache.remove(currentFxml);
            switchToWithoutContorller(currentFxml);
        }
    }

    /**
     * Позволяет заменить единственный экземпляр в тестах.
     *
     * @param mockSceneManager тестовый экземпляр
     */
    public static void setInstance(SceneManager mockSceneManager) {
        INSTANCE = mockSceneManager;
    }
}
