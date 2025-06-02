package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.chart.ChartDrawer;
import org.example.economicssimulatorclient.chart.ChartDrawerFactory;
import org.example.economicssimulatorclient.dto.CalculationRequestDto;
import org.example.economicssimulatorclient.dto.CalculationResponseDto;
import org.example.economicssimulatorclient.dto.EconomicModelDto;
import org.example.economicssimulatorclient.dto.ModelParameterDto;
import org.example.economicssimulatorclient.dto.ModelResultDto;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.ChartDataConverter;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelResultController extends BaseController {

    private final EconomicModelService modelService = get(EconomicModelService.class);

    @FXML
    private Button backButton;
    @FXML
    private Button mainButton;
    @FXML
    private VBox parameterList;
    @FXML
    private TextArea resultArea;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Pane chartPane;
    @FXML
    private Button saveButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Label statusLabel;

    private CalculationResponseDto response;
    private List<ModelParameterDto> parameters = new ArrayList<>();
    private EconomicModelDto model;
    private String selectedChartKey;

    /**
     * Вызови этот метод из SceneManager.switchTo("model_result.fxml", c -> c.initWithResult(...));
     */
    public void initWithResult(CalculationResponseDto response, EconomicModelDto model) {
        this.response = response;
        this.parameters = new ArrayList<>(response.updatedParameters());
        this.model = model; // EconomicModelDto из расчёта (добавь, если нет)
        clearStatusLabel();
        fillParameters();
        fillResult();
        fillCharts();
    }

    private void fillParameters() {
        parameterList.getChildren().clear();
        for (ModelParameterDto param : parameters) {
            VBox paramBox = new VBox(3);
            paramBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8; -fx-border-radius: 4; -fx-background-radius: 4;");
            paramBox.setSpacing(2);

            Label symbol = new Label(param.paramName());
            symbol.setStyle("-fx-font-weight: bold;");
            Label descr = new Label(param.description());
            descr.setWrapText(true);

            TextField value = new TextField(param.paramValue());
            value.setEditable(true);

            value.textProperty().addListener((obs, oldVal, newVal) -> {
                parameters.set(parameters.indexOf(param), new ModelParameterDto(
                        param.id(), param.modelId(), param.paramName(), param.paramType(),
                        newVal, param.displayName(), param.description(), param.customOrder()
                ));
            });

            paramBox.getChildren().addAll(symbol, descr, value);
            parameterList.getChildren().add(paramBox);
        }
    }

    private void fillResult() {
        ModelResultDto result = response.result();
        resultArea.setText(result != null && result.resultData() != null
                ? result.resultData() : "Нет результата");
    }

    private void fillCharts() {
        ModelResultDto result = response.result();
        if (result == null || result.resultData() == null) {
            chartPane.getChildren().clear();
            typeComboBox.getItems().clear();
            return;
        }
        // Получаем Map: chartKey -> Map с данными для визуализации
        Map<String, Map<String, Object>> chartDataMap = ChartDataConverter.parseRawChartData(result);

        typeComboBox.getItems().clear();
        typeComboBox.getItems().addAll(chartDataMap.keySet());
        if (!chartDataMap.isEmpty()) {
            typeComboBox.getSelectionModel().selectFirst();
            showChart(chartDataMap, typeComboBox.getItems().get(0));
        }

        typeComboBox.setOnAction(e -> showChart(chartDataMap, typeComboBox.getValue()));
    }

    private void showChart(Map<String, Map<String, Object>> chartDataMap, String chartKey) {
        chartPane.getChildren().clear();
        if (chartKey == null || !chartDataMap.containsKey(chartKey) || model == null) return;

        // Получаем визуализатор для нужной модели
        ChartDrawer drawer = ChartDrawerFactory.getDrawer(model.modelType());
        Node chart = drawer != null ? drawer.buildChart(chartKey, chartDataMap.get(chartKey)) : null;
        if (chart != null) {
            chartPane.getChildren().add(chart);
        } else {
            Label lbl = new Label("Нет визуализации для выбранного графика.");
            chartPane.getChildren().add(lbl);
        }
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.switchTo("model_view.fxml", (ModelViewController c) -> c.initWithModelId(model.id())));
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));

        repeatButton.setOnAction(e -> repeatCalculation());
        saveButton.setOnAction(e -> showError(statusLabel, "Сохранение отчёта пока не реализовано"));
    }

    private void repeatCalculation() {
        runAsync(() -> {
            try {
                CalculationRequestDto req = new CalculationRequestDto(
                        response.result().modelId(),
                        model.modelType(),
                        parameters
                );
                CalculationResponseDto resp = modelService.calculate(req);
                Platform.runLater(() -> initWithResult(resp, model));
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, "Ошибка при повторном расчете: " + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка при повторном расчете: " + ex.getMessage())));
    }

    @Override
    public void clearFields() {
        clearStatusLabel();
        resultArea.clear();
        chartPane.getChildren().clear();
        parameterList.getChildren().clear();
        typeComboBox.getItems().clear();
    }
}
