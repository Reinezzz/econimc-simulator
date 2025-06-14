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

import java.util.List;

/**
 * Контроллер главного окна приложения. Отображает список экономических
 * моделей и обеспечивает навигацию к экрану модели и работе с документами.
 */
public class MainController extends BaseController {

    private final AuthService authService = AuthService.getInstance();
    private final EconomicModelService modelService = get(EconomicModelService.class);
    @FXML
    public VBox tileDocuments;

    @FXML
    VBox modelList;

    @FXML
    private Button exitButton;

    @FXML
    private GridPane mainGrid;

    @FXML
    VBox tileButton;
    @FXML
    private Pane iconLastModel;

    @FXML
    protected Label statusLabel;

    private Long lastModelId = null;

    /**
     * Инициализирует главный экран: загружает список моделей, назначает
     * обработчики навигации и включает переключение языка.
     */
    @FXML
    private void initialize() {
        clearStatusLabel();
        loadModelList();
        initLangButton();
        long lastModelId = LastModelStorage.loadLastModelId();
        tileButton.setDisable(lastModelId == -1);
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
     * Загружает список экономических моделей и отображает его на экране.
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
     * Создаёт кнопки для каждой доступной модели.
     *
     * @param models список моделей
     */
    void populateModelList(List<EconomicModelDto> models) {
        modelList.getChildren().clear();
        for (EconomicModelDto model : models) {
            Button btn = new Button(localizedValue(model.name()));
            btn.getStyleClass().add("model-list-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            Tooltip tooltip = new Tooltip(model.description() != null ? model.description() : model.name());
            Tooltip.install(btn, tooltip);
            btn.setOnAction(e -> {
                lastModelId = model.id();
                SceneManager.switchTo("model_view.fxml", (ModelViewController c) -> c.initWithModelId(model.id()));
            });
            modelList.getChildren().add(btn);
        }
    }

    /**
     * Сбрасывает текст статуса на главном экране.
     */
    @Override
    public void clearFields() {
        clearStatusLabel();
    }

    /**
     * Выполняет выход пользователя и возвращает на экран авторизации.
     * Ошибки при выходе игнорируются.
     */
    @FXML
    private void onExitButtonClicked() {
        new Thread(() -> {
            try {
                authService.logout();
            } catch (Exception ignored) {
            }
            Platform.runLater(() -> {
                SceneManager.switchTo("authorization.fxml", c -> {
                    ((BaseController) c).clearStatusLabel();
                    ((BaseController) c).clearFields();
                });
            });
        }).start();
    }
}
