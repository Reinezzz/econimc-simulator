package org.example.economicssimulatorclient;

import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.init(primaryStage);
        SceneManager.switchTo("authorization.fxml");     // первая сцена
        primaryStage.setTitle("Economics Simulator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
