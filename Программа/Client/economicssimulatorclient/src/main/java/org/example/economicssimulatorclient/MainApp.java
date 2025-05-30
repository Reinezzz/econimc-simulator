package org.example.economicssimulatorclient;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.controller.MainController;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.util.SessionManager;

/**
 * Главный класс JavaFX-приложения Economics Simulator.
 * Отвечает за инициализацию и старт UI.
 */
public class MainApp extends Application {

    /**
     * Запускает главное окно приложения, инициализирует SceneManager.
     * @param primaryStage основной JavaFX-Stage
     * @throws Exception при ошибке инициализации
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BaseController.provide(AuthService.class, new AuthService());
        SceneManager.init(primaryStage);
        String refreshToken = SessionManager.getInstance().getRefreshToken();
        if (refreshToken != null) {
            // Пытаемся обновить accessToken асинхронно при наличии refreshToken
            new Thread(() -> {
                boolean refreshed = false;
                try {
                    refreshed = AuthService.getInstance().refreshTokens();
                } catch (Exception ex) {
                    // Можно залогировать ошибку обновления токена
                }
                boolean finalRefreshed = refreshed;
                Platform.runLater(() -> {
                    if (finalRefreshed) {
                        // Если refresh прошёл успешно — открываем основной экран
                        SceneManager.switchTo("main.fxml");
                    } else {
                        // Иначе открываем форму входа
                        SceneManager.switchTo("authorization.fxml");
                    }
                });
            }).start();
        } else {
            // Нет refreshToken — открываем форму входа
            SceneManager.switchTo("authorization.fxml");
        }

        primaryStage.setTitle("Economics Simulator");
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Точка входа приложения.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
