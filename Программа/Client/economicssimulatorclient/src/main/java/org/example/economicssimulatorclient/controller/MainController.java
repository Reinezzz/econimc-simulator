package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.List;

public class MainController {

    @FXML
    private VBox modelList;

    @FXML
    private Button addButton;

    @FXML
    private Button exitButton; // onExitButtonClicked уже реализован, НЕ трогаем!

    @FXML
    private GridPane mainGrid;

    // Для "Последняя модель"
    @FXML
    private Button tileButton; // первая кнопка в mainGrid
    @FXML
    private Pane iconLastModel;

    @FXML
    private void initialize() {

    }


    /**
     * Вернуть кнопку в mainGrid по позиции (row, col)
     */
    private Button getGridButton(int row, int col) {
        for (javafx.scene.Node node : mainGrid.getChildren()) {
            if (node instanceof Button b) {
                Integer r = GridPane.getRowIndex(b);
                Integer c = GridPane.getColumnIndex(b);
                if ((r == null ? 0 : r) == row && (c == null ? 0 : c) == col) {
                    return b;
                }
            }
        }
        return null;
    }

    // Обработчик выхода НЕ трогаем!
    @FXML
    private void onExitButtonClicked() {
        new Thread(() -> {
            try {
                // твоя логика выхода
            } catch (Exception ignored) {}
            Platform.runLater(() -> SceneManager.switchTo("authorization.fxml"));
        }).start();
    }
}
