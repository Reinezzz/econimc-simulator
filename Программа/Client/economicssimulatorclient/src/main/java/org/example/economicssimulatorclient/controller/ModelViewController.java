package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.LastModelStorage;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class ModelViewController extends BaseController {

    private final EconomicModelService modelService = get(EconomicModelService.class);

    private EconomicModelDto model;
    private List<ModelParameterDto> parameters = new ArrayList<>();
    private boolean editMode = false;
    private final List<TextField> valueFields = new ArrayList<>();

    @FXML
    private Button backButton;
    @FXML
    private Button mainButton;
    @FXML
    private VBox parameterList;
    @FXML
    private Label modelTitle;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button runButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addDocumentButton; // просто добавляем, не реализуем

    @FXML
    private Label statusLabel;

    private Long modelId;

    public void initWithModelId(Long id) {
        this.modelId = id;
        LastModelStorage.saveLastModelId(modelId);
        clearStatusLabel();
        loadModel();
    }

    private void loadModel() {
        runAsync(() -> {
            try {
                this.model = modelService.getModelById(modelId);
                this.parameters = modelService.getParametersByModelId(modelId);
                Platform.runLater(this::fillModelInfo);
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, "Ошибка загрузки: " + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка загрузки: " + ex.getMessage())));
    }

    private void fillModelInfo() {
        nameLabel.setText(model.name());
        typeLabel.setText(model.modelType());
        descriptionArea.setText(model.description() != null ? model.description() : "");
        modelTitle.setText(model.name());
        fillParameters();
        setEditMode(false);
    }

    private void fillParameters() {
        parameterList.getChildren().clear();
        valueFields.clear();
        for (ModelParameterDto param : parameters) {
            VBox paramBox = new VBox(3);
            paramBox.getStyleClass().add("parameter-card");
            paramBox.setSpacing(2);

            Label symbol = new Label(param.paramName());
            symbol.getStyleClass().add("parameter-name");
            Label descr = new Label(param.description());
            descr.getStyleClass().add("parameter-desc");
            descr.setWrapText(true);

            TextField value = new TextField(param.paramValue());
            value.setEditable(false);
            value.setPromptText("Значение");
            value.getStyleClass().add("parameter-input");
            valueFields.add(value);

            paramBox.getChildren().addAll(symbol, descr, value);
            parameterList.getChildren().add(paramBox);
        }
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));

        editButton.setOnAction(e -> toggleEditMode());

        runButton.setOnAction(e -> {
            if (editMode) {
                showError(statusLabel, "Сначала сохраните параметры");
                return;
            }
            runCalculation();
        });

        addDocumentButton.setOnAction(e -> {
            SceneManager.switchTo("documents.fxml", DocumentController::initialize);
        });
    }

    private void setEditMode(boolean enable) {
        this.editMode = enable;
        for (TextField value : valueFields) {
            value.setEditable(enable);
        }
        editButton.setText(enable ? "Сохранить" : "Редактировать");
        runButton.setDisable(enable);
    }

    private void toggleEditMode() {
        if (!editMode) {
            setEditMode(true);
        } else {
            // Сохраняем новые значения параметров
            List<ModelParameterDto> updated = new ArrayList<>();
            boolean valid = true;
            for (int i = 0; i < parameters.size(); i++) {
                ModelParameterDto p = parameters.get(i);
                String newValue = valueFields.get(i).getText();
                if (newValue == null || newValue.isEmpty()) {
                    showError(statusLabel, "Параметр " + p.paramName() + " не может быть пустым");
                    valid = false;
                    break;
                }
                updated.add(new ModelParameterDto(
                        p.id(),
                        p.modelId(),
                        p.paramName(),
                        p.paramType(),
                        newValue,
                        p.displayName(),
                        p.description(),
                        p.customOrder()
                ));
            }
            if (!valid) return;

            runAsync(() -> {
                try {
                    for (ModelParameterDto dto : updated) {
                        modelService.updateParameter(dto.id(), dto);
                    }
                    this.parameters = modelService.getParametersByModelId(modelId);
                    Platform.runLater(() -> {
                        showSuccess(statusLabel, "Параметры успешно сохранены");
                        fillParameters();
                        setEditMode(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError(statusLabel, "Ошибка сохранения: " + ex.getMessage()));
                }
            }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка сохранения: " + ex.getMessage())));
        }
    }

    private void runCalculation() {
        runAsync(() -> {
            try {
                CalculationRequestDto req = new CalculationRequestDto(
                        modelId, model.modelType(),  parameters
                );
                CalculationResponseDto resp = modelService.calculate(req);
                Platform.runLater(() ->
                        SceneManager.switchTo("model_result.fxml",
                                (ModelResultController c) -> c.initWithResult(resp, model)));
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, "Ошибка расчета: " + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка расчета: " + ex.getMessage())));
    }

    @Override
    public void clearFields() {
        clearStatusLabel();
        // reset all user-editable fields (если появятся)
    }
}
