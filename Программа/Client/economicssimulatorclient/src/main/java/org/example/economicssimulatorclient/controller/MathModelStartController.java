package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.ComputationService;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathModelStartController {

    @FXML private Button backButton;
    @FXML private Button mainButton;
    @FXML private Button runButton;
    @FXML private VBox paramsVBox;

    private MathModelDto mathModel;
    private final ComputationService computationService = new ComputationService();

    // Храним текущие значения параметров (name -> значение)
    private final Map<String, TextField> valueFields = new HashMap<>();

    public void setMathModel(MathModelDto model) {
        this.mathModel = model;
        fillParams();
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.back());
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml"));
        runButton.setOnAction(e -> onRun());
    }

    private void fillParams() {
        paramsVBox.getChildren().clear();
        valueFields.clear();
        if (mathModel == null || mathModel.parameters() == null) return;

        List<ModelParameterDto> params = mathModel.parameters();
        for (ModelParameterDto param : params) {
            HBox card = new HBox(8);
            TextField name = new TextField(param.name());
            name.setPromptText("Обозначение");
            name.setEditable(false);
            name.getStyleClass().add("text-field");

            TextField type = new TextField(param.paramType());
            type.setPromptText("Тип");
            type.setEditable(false);
            type.getStyleClass().add("text-field");

            TextField value = new TextField();
            value.setPromptText("Значение");
            value.getStyleClass().add("text-field");

            card.getChildren().addAll(name, type, value);
            paramsVBox.getChildren().add(card);
            valueFields.put(param.name(), value);
        }
    }

    private void onRun() {
        // Собираем значения параметров
        Map<String, String> values = new HashMap<>();
        boolean valid = true;
        for (Map.Entry<String, TextField> entry : valueFields.entrySet()) {
            String paramName = entry.getKey();
            TextField field = entry.getValue();
            String val = field.getText().trim();
            if (val.isEmpty()) {
                valid = false;
                highlight(field);
            } else {
                resetHighlight(field);
                values.put(paramName, val);
            }
        }

        if (!valid) {
            showAlert("Ошибка", "Заполните все значения параметров!");
            return;
        }

        runButton.setDisable(true);
        // Можно сделать отдельный DTO для передачи значений параметров, если сервер поддерживает
        new Thread(() -> {
            try {
                // Предполагается, что параметры передавать не требуется — только id модели
                // Если нужно передавать значения параметров — доработай ComputationService и сервер
                ComputationResultDto result = computationService.runComputation(mathModel.id());
                Platform.runLater(() -> {
                    runButton.setDisable(false);
                    // Передаём модель и значения на экран результатов, если требуется
                    SceneManager.switchToWithController("math_model_result.fxml", ctrl -> {
                        if (ctrl instanceof MathModelResultController c) {
                            c.setComputationResult(result, mathModel, values);
                        }
                    });
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    runButton.setDisable(false);
                    showAlert("Ошибка", "Ошибка вычисления:\n" + ex.getMessage());
                });
            }
        }).start();
    }

    private void highlight(Control ctrl) {
        ctrl.setStyle("-fx-border-color: red;");
    }
    private void resetHighlight(Control ctrl) {
        ctrl.setStyle("");
    }
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
