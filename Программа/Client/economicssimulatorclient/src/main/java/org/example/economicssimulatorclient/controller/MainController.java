package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Tooltip;
import org.example.economicssimulatorclient.dto.EconomicModelDto;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.LastModelStorage;
import org.example.economicssimulatorclient.util.SceneManager;
import org.example.economicssimulatorclient.util.SessionManager;

import java.util.List;

public class MainController extends BaseController {

    private final AuthService authService = AuthService.getInstance();
    private final EconomicModelService modelService = get(EconomicModelService.class);
    @FXML
    public VBox tileDocuments;

    @FXML
    private VBox modelList;

    @FXML
    private Button exitButton; // onExitButtonClicked уже реализован, НЕ трогаем!

    @FXML
    private GridPane mainGrid;

    // Для "Последняя модель"
    @FXML
    private VBox tileButton; // первая кнопка в mainGrid (fx:id добавлен в FXML)
    @FXML
    private Pane iconLastModel;

    @FXML
    protected Label statusLabel;

    // Для хранения id последней открытой модели
    private Long lastModelId = null;

    @FXML
    private void initialize() {
        clearStatusLabel();
        loadModelList();

        long lastModelId = LastModelStorage.loadLastModelId();
        tileButton.setDisable(lastModelId == -1);
        // "Последняя модель" — переход к просмотру последней модели, если есть
        tileButton.setOnMouseClicked(e -> {
            if (lastModelId != -1) {
                SceneManager.switchTo("model_view.fxml", (ModelViewController c) -> c.initWithModelId(lastModelId));
            } else {
                showError(statusLabel, "main.no_last_model");
            }
        });
        tileDocuments.setOnMouseClicked(e -> {
            SceneManager.switchTo("documents.fxml", c -> {
                ((DocumentController) c).initialize();
                ((DocumentController) c).fromView(false);
            });
        });
    }

    /**
     * Загружает список моделей и строит кнопки в левой панели.
     */
    public void loadModelList() {
        runAsync(() -> {
            try {
                List<EconomicModelDto> models = modelService.getAllModels();
                Platform.runLater(() -> populateModelList(models));
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, I18n.t("main.load_error") + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("main.load_error") + ex.getMessage())));
    }

    /**
     * Динамически создает кнопки для моделей, добавляет тултипы и обработчики перехода.
     */
    private void populateModelList(List<EconomicModelDto> models) {
        modelList.getChildren().clear();
        for (EconomicModelDto model : models) {
            Button btn = new Button(localizedValue(model.name()));
            btn.getStyleClass().add("model-list-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            Tooltip tooltip = new Tooltip(model.description() != null ? model.description() : model.name());
            Tooltip.install(btn, tooltip);
            btn.setOnAction(e -> {
                lastModelId = model.id();
                // Сохраняем в SessionManager — например, если нужно между сессиями
                // SessionManager.getInstance().setLastModelId(lastModelId);
                SceneManager.switchTo("model_view.fxml", (ModelViewController c) -> c.initWithModelId(model.id()));
            });
            modelList.getChildren().add(btn);
        }
    }

    @Override
    public void clearFields() {
        clearStatusLabel();
        // очищать можно поля поиска, если они будут добавлены
    }

    // Обработчик выхода НЕ трогаем!
    @FXML
    private void onExitButtonClicked() {
        new Thread(() -> {
            try {
                authService.logout();
            } catch (Exception ignored) {}
            Platform.runLater(() -> {
                SceneManager.switchTo("authorization.fxml",   c -> {
                    ((BaseController) c).clearStatusLabel();
                    ((BaseController) c).clearFields();
                });
            });
        }).start();
    }
}
