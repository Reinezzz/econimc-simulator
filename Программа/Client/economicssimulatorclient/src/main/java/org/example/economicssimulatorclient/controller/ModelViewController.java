package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.LastModelStorage;
import org.example.economicssimulatorclient.util.SceneManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
    private TextArea formulaArea;
    @FXML
    private Button runButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addDocumentButton;

    @FXML
    private Label statusLabel;

    @FXML
    private LlmChatComponentController llmChatComponent;

    @FXML
    private VBox chat;

    private Long modelId;


    private List<ModelParameterDto> pendingExtractionParams = null;

    public void initWithModelId(Long id) {
        initWithModelId(id, null);
    }

    public void initWithModelId(Long id, List<ModelParameterDto> extractedParams) {
        this.modelId = id;
        LastModelStorage.saveLastModelId(modelId);
        clearStatusLabel();
        this.pendingExtractionParams = extractedParams;
        loadModel();
        if (llmChatComponent != null) {
            llmChatComponent.clear();
        }
        try {
            I18n.setLocale(Locale.forLanguageTag("ru"));
            Locale locale = I18n.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/economicssimulatorclient/llm_chat_component.fxml"));
            loader.setResources(bundle);
            VBox chatWindow = loader.load();
            chat.getChildren().add(chatWindow);
            llmChatComponent = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModel() {
        runAsync(() -> {
            try {
                this.model = modelService.getModelById(modelId);
                this.parameters = modelService.getParametersByModelId(modelId);
                Platform.runLater(this::fillModelInfo);
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, I18n.t("model.load_error") + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("model.load_error") + ex.getMessage())));
    }

    private void fillModelInfo() {
        nameLabel.setText(localizedValue(model.name()));
        typeLabel.setText(localizedValue(model.modelType()));
        descriptionArea.setText(model.description() != null ? localizedValue(model.description()) : "");
        formulaArea.setText(model.formula() != null ? model.formula() : "");
        modelTitle.setText(localizedValue(model.name()));

        if (pendingExtractionParams != null && !pendingExtractionParams.isEmpty()) {
            fillParametersFromExtraction(pendingExtractionParams);
            pendingExtractionParams = null;
        } else {
            fillParameters();
        }
        setEditMode(false);
        setupLlmChatComponent();
    }

    public void fillParametersFromExtraction(List<ModelParameterDto> newParams) {
        if (newParams == null || newParams.isEmpty()) return;
        for (int i = 0; i < parameters.size(); i++) {
            ModelParameterDto oldParam = parameters.get(i);
            String newValue = oldParam.paramValue();
            for (ModelParameterDto newParam : newParams) {
                if (oldParam.paramName().equals(newParam.paramName())) {
                    newValue = String.valueOf(newParam.paramValue());
                    break;
                }
            }
            parameters.set(i, new ModelParameterDto(
                    oldParam.id(), oldParam.modelId(), oldParam.paramName(), oldParam.paramType(),
                    newValue, oldParam.displayName(), oldParam.description(), oldParam.customOrder()
            ));
        }
        fillParameters();
        showSuccess(statusLabel, "model.params_from_doc");
    }

    private void fillParameters() {
        parameterList.getChildren().clear();
        valueFields.clear();
        for (ModelParameterDto param : parameters) {
            VBox paramBox = new VBox(3);
            paramBox.getStyleClass().add("parameter-card");
            paramBox.setSpacing(2);

            Label symbol = new Label(localizedValue(param.paramName()));
            symbol.getStyleClass().add("parameter-name");
            Label descr = new Label(localizedValue(param.description()));
            descr.getStyleClass().add("parameter-desc");
            descr.setWrapText(true);

            TextField value = new TextField(param.paramValue());
            value.setEditable(false);
            value.setPromptText("Значение");
            value.setPromptText(I18n.t("model.value_placeholder"));
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
                showError(statusLabel, "model.save_params_first");
                return;
            }
            runCalculation();
        });

        addDocumentButton.setOnAction(e -> {
            SceneManager.switchTo("documents.fxml", c -> {
                ((DocumentController) c).initialize();
                ((DocumentController) c).fromView(true);
            });
        });
    }

    private void setEditMode(boolean enable) {
        this.editMode = enable;
        for (TextField value : valueFields) {
            value.setEditable(enable);
        }
        editButton.setText(enable ? I18n.t("model.save") : I18n.t("model.edit"));
        runButton.setDisable(enable);
    }

    private void toggleEditMode() {
        if (!editMode) {
            setEditMode(true);
        } else {
            List<ModelParameterDto> updated = new ArrayList<>();
            boolean valid = true;
            for (int i = 0; i < parameters.size(); i++) {
                ModelParameterDto p = parameters.get(i);
                String newValue = valueFields.get(i).getText();
                if (newValue == null || newValue.isEmpty()) {
                    showError(statusLabel, I18n.t("model.param_empty") + p.paramName());
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
                        showSuccess(statusLabel, "model.params_saved");
                        fillParameters();
                        setEditMode(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError(statusLabel, I18n.t("model.save_error") + ex.getMessage()));
                }
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("model.save_error") + ex.getMessage())));
        }
    }

    private void runCalculation() {
        runAsync(() -> {
            try {
                CalculationRequestDto req = new CalculationRequestDto(
                        modelId, model.modelType(), parameters
                );
                CalculationResponseDto resp = modelService.calculate(req);
                Platform.runLater(() ->
                        SceneManager.switchTo("model_result.fxml",
                                (ModelResultController c) -> c.initWithResult(resp, model)));
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, I18n.t("model.calc_error") + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("model.calc_error") + ex.getMessage())));
    }

    private void setupLlmChatComponent() {
        if (llmChatComponent == null) return;
        llmChatComponent.setRequestSupplier(() -> new org.example.economicssimulatorclient.dto.LlmChatRequestDto(
                modelId,
                "",
                new ArrayList<>(parameters),
                null,
                null
        ));
    }

    @Override
    public void clearFields() {
        clearStatusLabel();
        if (llmChatComponent != null) {
            llmChatComponent.clear();
        }
    }

}
