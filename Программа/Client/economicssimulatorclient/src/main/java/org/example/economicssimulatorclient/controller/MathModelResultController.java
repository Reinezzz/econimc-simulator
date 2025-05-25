package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.ComputationService;
import org.example.economicssimulatorclient.service.MathModelService;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.*;

public class MathModelResultController {

    @FXML private Button backButton;
    @FXML private Button mainButton;
    @FXML private VBox paramsVBox;
    @FXML private Button editParamsButton;
    @FXML private TextArea resultArea;
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private Pane chartPane;
    @FXML private Button aiButton;
    @FXML private Button saveReportButton;
    @FXML private Button repeatButton;

    // Для редактирования значений параметров
    private final Map<String, TextField> valueFields = new HashMap<>();
    private boolean paramsEditable = false;

    // Данные для отображения
    private MathModelDto mathModel;
    private ComputationResultDto computationResult;
    private Map<String, String> paramValues; // Имя -> значение
    private final AuthService auth = BaseController.get(AuthService.class);

    private final ComputationService computationService = new ComputationService(auth);

    // Пример типов графиков (можно добавить любые три универсальных)
    private static final List<String> chartTypes = List.of("Линейный график", "Гистограмма", "Точечная диаграмма");

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.back());
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml"));
        aiButton.setOnAction(e -> {}); // Не реализовано
        saveReportButton.setOnAction(e -> {}); // Не реализовано

        editParamsButton.setOnAction(e -> onEditParams());
        repeatButton.setOnAction(e -> onRepeat());

        chartTypeComboBox.getItems().addAll(chartTypes);
        chartTypeComboBox.setValue(chartTypes.get(0));
        chartTypeComboBox.setOnAction(e -> showChart());
    }

    /** Вызывается при переходе с предыдущего экрана */
    public void setComputationResult(ComputationResultDto result, MathModelDto model, Map<String, String> values) {
        this.computationResult = result;
        this.mathModel = model;
        this.paramValues = new HashMap<>(values);
        fillParams();
        fillResult();
        showChart();
        setParamsEditable(false);
    }

    private void fillParams() {
        paramsVBox.getChildren().clear();
        valueFields.clear();
        if (mathModel == null || mathModel.parameters() == null) return;
        for (ModelParameterDto param : mathModel.parameters()) {
            HBox card = new HBox(8);

            TextField nameField = new TextField(param.name());
            nameField.setPromptText("Обозначение");
            nameField.setEditable(false);
            nameField.getStyleClass().add("text-field");

            TextField descField = new TextField(param.description());
            descField.setPromptText("Описание");
            descField.setEditable(false);
            descField.getStyleClass().add("text-field");

            TextField valueField = new TextField(paramValues != null ? paramValues.getOrDefault(param.name(), "") : "");
            valueField.setPromptText("Значение");
            valueField.getStyleClass().add("text-field");
            valueField.setEditable(false);

            card.getChildren().addAll(nameField, descField, valueField);
            paramsVBox.getChildren().add(card);
            valueFields.put(param.name(), valueField);
        }
    }

    private void fillResult() {
        if (computationResult != null && computationResult.resultData() != null) {
            resultArea.setText(computationResult.resultData());
        } else {
            resultArea.setText("");
        }
    }

    /** Кнопка "Изменить" — разрешает редактировать только значения параметров */
    private void onEditParams() {
        setParamsEditable(true);
        editParamsButton.setDisable(true);
        repeatButton.setDisable(false);
    }

    private void setParamsEditable(boolean editable) {
        for (TextField f : valueFields.values()) {
            f.setEditable(editable);
        }
        paramsEditable = editable;
    }

    /** Кнопка "Повторить" — заново запускает вычисления с новыми значениями */
    private void onRepeat() {
        if (!paramsEditable) return;
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
        repeatButton.setDisable(true);
        // (Можно отправить значения параметров на сервер, если сервер их принимает)
        new Thread(() -> {
            try {
                ComputationResultDto result = computationService.runComputation(mathModel.id());
                Platform.runLater(() -> {
                    computationResult = result;
                    paramValues = values;
                    fillResult();
                    showChart();
                    setParamsEditable(false);
                    editParamsButton.setDisable(false);
                    repeatButton.setDisable(true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    repeatButton.setDisable(false);
                    showAlert("Ошибка", "Ошибка вычисления:\n" + ex.getMessage());
                });
            }
        }).start();
    }

    /** Пример отображения графика — здесь подбирается тип, но ты можешь доработать под данные своей модели */
    private void showChart() {
        chartPane.getChildren().clear();
        String type = chartTypeComboBox.getValue();
        if (type == null || computationResult == null) return;

        // Демонстрационный график — подбери преобразование resultData или paramValues под свои данные!
        Chart chart = null;
        if (type.equals("Линейный график")) {
            chart = buildLineChart();
        } else if (type.equals("Гистограмма")) {
            chart = buildBarChart();
        } else if (type.equals("Точечная диаграмма")) {
            chart = buildScatterChart();
        }
        if (chart != null) {
            chart.setPrefHeight(240);
            chart.setPrefWidth(460);
            chartPane.getChildren().add(chart);
        }
    }

    private LineChart<String, Number> buildLineChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, TextField> entry : valueFields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getText();
            try {
                double val = Double.parseDouble(value);
                series.getData().add(new XYChart.Data<>(key, val));
            } catch (NumberFormatException ignored) {}
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    private BarChart<String, Number> buildBarChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, TextField> entry : valueFields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getText();
            try {
                double val = Double.parseDouble(value);
                series.getData().add(new XYChart.Data<>(key, val));
            } catch (NumberFormatException ignored) {}
        }
        barChart.getData().add(series);
        return barChart;
    }

    private ScatterChart<String, Number> buildScatterChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, TextField> entry : valueFields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getText();
            try {
                double val = Double.parseDouble(value);
                series.getData().add(new XYChart.Data<>(key, val));
            } catch (NumberFormatException ignored) {}
        }
        scatterChart.getData().add(series);
        return scatterChart;
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
