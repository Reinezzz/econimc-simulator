package org.example.economicssimulatorclient;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.example.economicssimulatorclient.controller.AuthorizationController;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.controller.MainController;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.EconomicModelService;
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
        boolean justLoggedOut = SessionManager.getInstance().isJustLoggedOut();
        if (refreshToken != null && !justLoggedOut) {
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
                        BaseController.provide(EconomicModelService.class, new EconomicModelService());
                        // Если refresh прошёл успешно — открываем основной экран
                        SceneManager.switchToWithoutContorller("main.fxml");
                    } else {
                        // Иначе открываем форму входа
                        SceneManager.switchTo("authorization.fxml",   c -> {
                            ((BaseController) c).clearStatusLabel();
                            ((BaseController) c).clearFields();
                        });
                    }
                });
            }).start();
        } else {
            // Нет refreshToken — открываем форму входа
            SceneManager.switchTo("authorization.fxml",   c -> {
                ((BaseController) c).clearStatusLabel();
                ((BaseController) c).clearFields();
            });
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
