package org.example.economicssimulatorclient;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        SceneManager.switchTo("authorization.fxml");     // первая сцена
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
