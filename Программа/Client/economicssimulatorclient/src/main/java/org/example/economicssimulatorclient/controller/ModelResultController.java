package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.example.economicssimulatorclient.chart.ChartDrawer;
import org.example.economicssimulatorclient.chart.ChartDrawerFactory;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.parser.ParserFactory;
import org.example.economicssimulatorclient.parser.ResultParser;
import org.example.economicssimulatorclient.util.*;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.service.ReportService;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Отображает результаты расчёта экономической модели: текстовые данные,
 * графики и чат с ассистентом. Позволяет повторить расчёт и сохранить
 * результаты в виде отчёта.
 */
public class ModelResultController extends BaseController {

    private final EconomicModelService modelService = get(EconomicModelService.class);
    private final ReportService reportService = new ReportService(URI.create("http://localhost:8080"));

    @FXML
    private Button backButton;
    @FXML
    private Button mainButton;
    @FXML
    VBox parameterList;
    @FXML
    TextArea resultArea;
    @FXML
    ComboBox<String> typeComboBox;
    @FXML
    Pane chartPane;
    @FXML
    Button saveButton;
    @FXML
    private Button repeatButton;
    @FXML
    Label statusLabel;
    @FXML
    VBox chat;

    @FXML
    private LlmChatComponentController llmChatComponent;

    private CalculationResponseDto response;
    List<ModelParameterDto> parameters = new ArrayList<>();
    private EconomicModelDto model;
    private String selectedChartKey;
    Map<String, Map<String, Object>> chartDataMap = new HashMap<>();
    private final Map<String, Node> chartNodes = new HashMap<>();
    private final Set<String> selectedAssistantMessages = new LinkedHashSet<>();

    /**
     * Инициализирует контроллер полученными результатами расчёта.
     *
     * @param response ответ сервера с расчётами
     * @param model    модель, по которой производился расчёт
     */
    public void initWithResult(CalculationResponseDto response, EconomicModelDto model) {
        this.response = response;
        this.parameters = new ArrayList<>(response.updatedParameters());
        this.model = model;
        clearStatusLabel();
        fillParameters();
        fillResult();
        fillCharts();
        chat.getChildren().clear();
        try {
            Locale locale = I18n.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/economicssimulatorclient/llm_chat_component.fxml"));
            loader.setResources(bundle);
            VBox chatWindow = loader.load();
            chat.getChildren().add(chatWindow);
            llmChatComponent = loader.getController();
            llmChatComponent.setOnAssistantMessageAppended(this::setupAddToReportButtonForChat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupLlmChatComponent();

        setupAddToReportButtonForChat();
    }

    /**
     * Проверяет значения параметров перед повторным расчётом.
     *
     * @return {@code true}, если все параметры корректны
     */
    boolean validateParameters() {
        for (ModelParameterDto p : parameters) {
            if (!ParameterValidator.notEmpty(p.paramValue())) {
                showError(statusLabel, I18n.t("model.param_empty") + p.paramName());
                return false;
            }
            if (!ParameterValidator.isValid(p)) {
                showError(statusLabel, I18n.t("model.param_invalid") + p.paramName());
                return false;
            }
        }
        return true;
    }

    /**
     * Отображает текущие параметры модели и подготавливает поля для их правки.
     */
    private void fillParameters() {
        parameterList.getChildren().clear();
        for (int i = 0; i < parameters.size(); i++) {
            ModelParameterDto param = parameters.get(i);
            int index = i;
            VBox paramBox = new VBox(3);
            paramBox.getStyleClass().add("parameter-card");
            Label symbol = new Label(localizedValue(param.paramName()));
            symbol.getStyleClass().add("parameter-name");
            Label descr = new Label(localizedValue(param.description()));
            descr.getStyleClass().add("parameter-desc");
            descr.setWrapText(true);
            Label type = new Label(param.paramType());
            type.getStyleClass().add("parameter-type");

            TextField value = new TextField(param.paramValue());
            value.setEditable(true);
            value.getStyleClass().add("parameter-input");

            value.textProperty().addListener((obs, oldVal, newVal) -> {
                parameters.set(index, new ModelParameterDto(
                        param.id(), param.modelId(), param.paramName(), param.paramType(),
                        newVal, param.displayName(), param.description(), param.customOrder()
                ));
            });

            paramBox.getChildren().addAll(symbol, descr, value);
            parameterList.getChildren().add(paramBox);
        }
    }

    /**
     * Показывает текстовый результат расчёта в соответствующем поле.
     */
    private void fillResult() {
        ModelResultDto result = response.result();
        ResultParser parser = ParserFactory.getParser(model.modelType());
        resultArea.setText(parser.parse(result.resultData()));
    }

    /**
     * Подготавливает набор графиков и список их типов.
     */
    private void fillCharts() {
        ModelResultDto result = response.result();
        if (result == null || result.resultData() == null) {
            chartPane.getChildren().clear();
            typeComboBox.getItems().clear();
            chartDataMap.clear();
            return;
        }
        chartDataMap = ChartDataConverter.parseRawChartData(result);
        chartNodes.clear();

        typeComboBox.getItems().clear();
        typeComboBox.getItems().addAll(chartDataMap.keySet());
        typeComboBox.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : I18n.t("chart." + item));
            }
        });
        typeComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : I18n.t("chart." + item));
            }
        });
        if (!chartDataMap.isEmpty()) {
            typeComboBox.getSelectionModel().selectFirst();
            showChart(chartDataMap, typeComboBox.getItems().get(0));
        }

        typeComboBox.setOnAction(e -> showChart(chartDataMap, typeComboBox.getValue()));
    }

    /**
     * Отображает график по ключу в панели визуализации.
     *
     * @param chartDataMap набор всех доступных графиков
     * @param chartKey     ключ выбранного графика
     */
    private void showChart(Map<String, Map<String, Object>> chartDataMap, String chartKey) {
        chartPane.getChildren().clear();
        this.selectedChartKey = chartKey;
        if (chartKey == null || !chartDataMap.containsKey(chartKey) || model == null) return;
        ChartDrawer drawer = ChartDrawerFactory.getDrawer(model.modelType());
        Node chart = drawer != null ? drawer.buildChart(chartKey, chartDataMap.get(chartKey)) : null;
        if (chart != null) {
            chartPane.getChildren().add(chart);
            chartNodes.put(chartKey, chart);
            if (chart instanceof Region region) {
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                region.setMinSize(0, 0);
                region.prefWidthProperty().bind(chartPane.widthProperty());
                region.prefHeightProperty().bind(chartPane.heightProperty());
            }
        } else {
            Label lbl = new Label(I18n.t("chart.no_visualization"));
            chartPane.getChildren().add(lbl);
        }
    }

    /**
     * Назначает обработчики кнопок навигации, повторного расчёта и сохранения
     * отчётов.
     */
    /**
     * Назначает обработчики кнопок навигации и сохранения отчёта.
     */
    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.switchTo("model_view.fxml", (ModelViewController c) -> c.initWithModelId(model.id())));
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));
        repeatButton.setOnAction(e -> repeatCalculation());
        saveButton.setOnAction(e -> onSaveReport());
    }

    /**
     * Запускает повторный расчёт с текущими параметрами.
     */
    private void repeatCalculation() {
        if (!validateParameters()) {
            return;
        }
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
                Platform.runLater(() -> showError(statusLabel, "error.repeat_calculations" + ex.getMessage()));
            }
        }, ex -> Platform.runLater(() -> showError(statusLabel, "error.repeat_calculations" + ex.getMessage())));
    }

    /**
     * Настраивает компонент чата для работы с текущими данными расчёта.
     */
    private void setupLlmChatComponent() {
        if (llmChatComponent == null) return;
        llmChatComponent.setRequestSupplier(() -> new LlmChatRequestDto(
                model.id(),
                "",
                new ArrayList<>(parameters),
                buildVisualizations(),
                response.result()
        ));
    }

    /**
     * Формирует список визуализаций для передачи в LLM.
     *
     * @return список визуализаций
     */
    List<LlmVisualizationDto> buildVisualizations() {
        List<LlmVisualizationDto> visualizations = new ArrayList<>();
        if (chartDataMap != null && !chartDataMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : chartDataMap.entrySet()) {
                String chartKey = entry.getKey();
                Map<String, Object> data = entry.getValue();
                String chartTitle = I18n.t("chart." + chartKey);
                visualizations.add(new LlmVisualizationDto(chartKey, chartTitle, data));
            }
        }
        return visualizations;
    }

    /**
     * Добавляет к сообщениям ассистента кнопки выбора для отчёта.
     */
    private void setupAddToReportButtonForChat() {
        Platform.runLater(() -> {
            if (llmChatComponent == null) return;
            VBox chatArea = llmChatComponent.getChatArea();
            if (chatArea == null) return;

            for (Node node : chatArea.getChildren()) {
                if (node instanceof HBox row) {
                    if (row.getStyleClass().contains("llm-chat-msg-row-assistant")) {
                        Label lbl = (Label) row.getChildren().get(0);
                        String messageText = lbl.getText();
                        boolean alreadyHasButton = row.getChildren().stream().anyMatch(child ->
                                child instanceof Button btn &&
                                        ("+".equals(btn.getText()) || "-".equals(btn.getText()))
                        );
                        if (alreadyHasButton) continue;
                        Button addBtn = new Button(selectedAssistantMessages.contains(messageText) ? "-" : "+");
                        addBtn.setVisible(false);
                        addBtn.getStyleClass().add("llm-add-report-btn");
                        addBtn.setOnAction(e -> {
                            if (selectedAssistantMessages.contains(messageText)) {
                                selectedAssistantMessages.remove(messageText);
                                addBtn.setText("+");
                            } else {
                                selectedAssistantMessages.add(messageText);
                                addBtn.setText("-");
                            }
                        });
                        row.setOnMouseEntered(ev -> addBtn.setVisible(true));
                        row.setOnMouseExited(ev -> addBtn.setVisible(false));
                        row.getChildren().add(addBtn);
                    }
                }
            }
        });
    }

    /**
     * Сохраняет результаты в виде PDF-отчёта. Собирает изображения всех
     * графиков и выбранные сообщения чата. На время выполнения кнопка
     * сохранения блокируется.
     */
    /**
     * Создаёт PDF-отчёт на основании текущих данных и выбранных сообщений.
     * Кнопка сохранения блокируется на время выполнения.
     */
    @FXML
    private void onSaveReport() {
        if (!validateParameters()) {
            return;
        }
        saveButton.setDisable(true);

        String prevSelectedKey = typeComboBox.getSelectionModel().getSelectedItem();

        List<ReportChartImageDto> chartImages = new ArrayList<>();
        List<String> chartKeys = new ArrayList<>(chartDataMap.keySet());

        Platform.runLater(() -> {
            for (String chartKey : chartKeys) {
                typeComboBox.getSelectionModel().select(chartKey);
                showChart(chartDataMap, chartKey);
                chartPane.layout();
                Node chartNode = chartPane.getChildren().isEmpty() ? null : chartPane.getChildren().get(0);
                if (chartNode != null) {
                    String base64 = ReportImageUtil.toBase64Png(chartNode, 800, 300);
                    chartImages.add(new ReportChartImageDto(I18n.t("chart." + chartKey), base64));
                }
            }

            if (prevSelectedKey != null && chartDataMap.containsKey(prevSelectedKey)) {
                typeComboBox.getSelectionModel().select(prevSelectedKey);
                showChart(chartDataMap, prevSelectedKey);
            }

            List<LlmChatResponseDto> selectedLlmResponses = new ArrayList<>();
            for (String msg : selectedAssistantMessages) {
                selectedLlmResponses.add(new LlmChatResponseDto(msg));
            }
            String parsedResult = resultArea.getText();

            ReportCreateRequestDto req = new ReportCreateRequestDto(
                    model.id(),
                    localizedValue(model.name()),
                    "",
                    I18n.getLocale().getLanguage(),
                    new ArrayList<>(parameters),
                    response.result(),
                    chartImages,
                    selectedLlmResponses,
                    parsedResult
            );

            runAsync(() -> {
                try {
                    reportService.createReport(req);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    saveButton.setDisable(false);
                });
            }, ex -> Platform.runLater(() -> {
                saveButton.setDisable(false);
            }));
        });
    }

    /**
     * Сбрасывает все элементы интерфейса: параметры, текст результата и чат.
     */
    @Override
    public void clearFields() {
        clearStatusLabel();
        resultArea.clear();
        chartPane.getChildren().clear();
        parameterList.getChildren().clear();
        typeComboBox.getItems().clear();
        chat.getChildren().clear();
        selectedAssistantMessages.clear();
    }
}
