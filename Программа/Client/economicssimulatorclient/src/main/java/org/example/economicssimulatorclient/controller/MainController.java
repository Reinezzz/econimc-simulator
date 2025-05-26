package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.MathModelService;
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


    private final MathModelService mathModelService = new MathModelService();

    private MathModelDto lastOpenedModel = null;

    @FXML
    private void initialize() {
        // Загрузить список моделей при инициализации
        loadModels();

        addButton.setOnAction(e -> {
            // Переход на экран создания модели
            SceneManager.switchToWithController("create_math_model.fxml", ctrl -> {
                if (ctrl instanceof org.example.economicssimulatorclient.controller.CreateMathModelController c) {
                    c.setEditModel(null);
                }
            });
        });

        // Первая плитка ("Последняя модель") — привязываем клик
        Button lastModelTile = getGridButton(0, 0);
        if (lastModelTile != null) {
            lastModelTile.setOnAction(e -> {
                if (lastOpenedModel != null) {
                    SceneManager.switchToWithController("math_model_view.fxml", ctrl -> {
                        if (ctrl instanceof org.example.economicssimulatorclient.controller.MathModelViewController c) {
                            c.setMathModel(lastOpenedModel);
                        }
                    });
                }
            });
        }
    }

    /**
     * Загрузка списка моделей и формирование кнопок в modelList.
     */
    private void loadModels() {
        modelList.getChildren().clear();

        new Thread(() -> {
            try {
                List<MathModelDto> models = mathModelService.getAllModels();
                Platform.runLater(() -> {
                    for (MathModelDto model : models) {
                        Button btn = new Button(model.name());
                        btn.setMaxWidth(Double.MAX_VALUE);
                        btn.getStyleClass().add("model-list-btn");
                        btn.setTooltip(new Tooltip(model.name())); // Показывает полное название
                        btn.setOnAction(ev -> {
                            lastOpenedModel = model;
                            SceneManager.switchToWithController("math_model_view.fxml", ctrl -> {
                                if (ctrl instanceof org.example.economicssimulatorclient.controller.MathModelViewController c) {
                                    c.setMathModel(model);
                                }
                            });
                        });
                        modelList.getChildren().add(btn);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
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
