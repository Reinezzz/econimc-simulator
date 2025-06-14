package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionRequestDto;
import org.example.economicssimulatorclient.dto.LlmParameterExtractionResponseDto;
import org.example.economicssimulatorclient.dto.ReportListItemDto;
import org.example.economicssimulatorclient.service.DocumentService;
import org.example.economicssimulatorclient.service.LlmService;
import org.example.economicssimulatorclient.service.ReportService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.LastModelStorage;
import org.example.economicssimulatorclient.util.SceneManager;

import java.io.*;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Управляет списком документов и отчётов. Позволяет загружать,
 * удалять, скачивать и выбирать документы. При вызове из просмотра модели
 * поддерживает извлечение параметров из документа.
 */
public class DocumentController extends BaseController {

    @FXML
    Button backButton;
    @FXML
    Button mainButton;
    @FXML
    Button addButton;
    @FXML
    Button deleteButton;
    @FXML
    Button selectButton;
    @FXML
    ComboBox<String> typeComboBox;
    @FXML
    GridPane tableGrid;
    @FXML
    protected Label statusLabel;

    private final DocumentService documentService = new DocumentService();
    private final ReportService reportService = new ReportService(URI.create("http://92.118.85.123:8080")); // замени на свой адрес

    final List<CheckBox> checkBoxes = new ArrayList<>();
    final List<DocumentDto> documents = new ArrayList<>();
    private final List<ReportListItemDto> reports = new ArrayList<>();
    private boolean fromView = false;

    private Long modelId; // id текущей экономической модели (если пришли из ModelView)

    /**
     * Инициализирует элементы интерфейса и загружает начальную таблицу
     * документов или отчётов. Также назначает обработчики кнопок и
     * включает поддержку смены языка.
     */
    @FXML
    public void initialize() {
        backButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));

        typeComboBox.getItems().setAll(
                I18n.t("docs.type.documents"),
                I18n.t("docs.type.reports")
        );
        typeComboBox.getSelectionModel().selectFirst();
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> loadTable());

        addButton.setOnAction(e -> onAdd());
        deleteButton.setOnAction(e -> onDelete());
        selectButton.setVisible(false);
        selectButton.setOnAction(e -> onSelect());
    }

    /**
     * Включает режим выбора документа для извлечения параметров.
     *
     * @param isVisible {@code true}, если контроллер открыт из просмотра модели
     */
    public void fromView(boolean isVisible) {
        this.fromView = isVisible;
        selectButton.setVisible(isVisible);
        if (isVisible) {
            this.modelId = LastModelStorage.loadLastModelId();
        }
    }

    /**
     * Загружает таблицу документов или отчётов в зависимости от выбранного типа.
     */
    private void loadTable() {
        String selectedType = typeComboBox.getValue();
        if (I18n.t("docs.type.documents").equals(selectedType)) {
            loadDocuments();
            return;
        }
        if (I18n.t("docs.type.reports").equals(selectedType)) {
            loadReports();
            return;
        }
    }

    /**
     * Получает список документов пользователя и отображает его в таблице.
     */
    private void loadDocuments() {
        tableGrid.getChildren().clear();
        checkBoxes.clear();
        documents.clear();
        clearStatusLabel();
        addTableHeader(false);

        runAsync(() -> {
            List<DocumentDto> docs;
            try {
                docs = documentService.getUserDocuments();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> fillDocumentsTable(docs));
        }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.loading_error") + ex.getMessage())));
    }

    /**
     * Заполняет таблицу строками документов.
     *
     * @param docs полученный список документов
     */
    void fillDocumentsTable(List<DocumentDto> docs) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        int row = 1;
        checkBoxes.clear();
        documents.clear();
        for (DocumentDto doc : docs) {
            CheckBox cb = new CheckBox();
            cb.getStyleClass().addAll("table-checkbox", "table-cell", "col-checkbox");
            cb.setMaxWidth(Double.MAX_VALUE);

            Label dateLabel = new Label(doc.uploadedAt() != null ? doc.uploadedAt().format(fmt) : "");
            dateLabel.getStyleClass().addAll("table-cell");
            dateLabel.setMaxWidth(Double.MAX_VALUE);
            dateLabel.setAlignment(javafx.geometry.Pos.CENTER);

            Label nameLabel = new Label(doc.name());
            nameLabel.getStyleClass().add("table-cell");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(javafx.geometry.Pos.CENTER);

            Button downloadBtn = new Button("⤓");
            downloadBtn.getStyleClass().addAll("download-button", "table-cell", "col-download", "table-cell-last");
            downloadBtn.setMaxWidth(Double.MAX_VALUE);

            final long docId = doc.id();
            final String docName = doc.name();
            downloadBtn.setOnAction(ev -> onDownloadDocument(docId, docName));

            tableGrid.add(cb, 0, row);
            tableGrid.add(dateLabel, 1, row);
            tableGrid.add(nameLabel, 2, row);
            tableGrid.add(downloadBtn, 3, row);

            GridPane.setHgrow(cb, Priority.NEVER);
            GridPane.setHgrow(dateLabel, Priority.ALWAYS);
            GridPane.setHgrow(nameLabel, Priority.ALWAYS);
            GridPane.setHgrow(downloadBtn, Priority.NEVER);

            checkBoxes.add(cb);
            documents.add(doc);
            row++;
        }
    }

    /**
     * Загружает и отображает список отчётов пользователя.
     */
    private void loadReports() {
        tableGrid.getChildren().clear();
        checkBoxes.clear();
        reports.clear();
        clearStatusLabel();
        addTableHeader(true);

        runAsync(() -> {
            List<ReportListItemDto> list;
            try {
                list = reportService.getReports();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> fillReportsTable(list));
        }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.reports_loading_error") + ex.getMessage())));
    }

    /**
     * Заполняет таблицу отчётов данными.
     *
     * @param reportList полученный список отчётов
     */
    private void fillReportsTable(List<ReportListItemDto> reportList) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        int row = 1;
        checkBoxes.clear();
        reports.clear();
        for (ReportListItemDto report : reportList) {
            CheckBox cb = new CheckBox();
            cb.getStyleClass().addAll("table-checkbox", "table-cell", "col-checkbox");
            cb.setMaxWidth(Double.MAX_VALUE);

            Label dateLabel = new Label(report.createdAt() != null ? report.createdAt().format(fmt) : "");
            dateLabel.getStyleClass().addAll("table-cell");
            dateLabel.setMaxWidth(Double.MAX_VALUE);
            dateLabel.setAlignment(javafx.geometry.Pos.CENTER);

            Label nameLabel = new Label(report.name());
            nameLabel.getStyleClass().add("table-cell");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(javafx.geometry.Pos.CENTER);

            Button downloadBtn = new Button("⤓");
            downloadBtn.getStyleClass().addAll("download-button", "table-cell", "col-download", "table-cell-last");
            downloadBtn.setMaxWidth(Double.MAX_VALUE);

            final long reportId = report.id();
            final String reportName = report.name() + ".pdf";
            downloadBtn.setOnAction(ev -> onDownloadReport(reportId, reportName));

            tableGrid.add(cb, 0, row);
            tableGrid.add(dateLabel, 1, row);
            tableGrid.add(nameLabel, 2, row);
            tableGrid.add(downloadBtn, 3, row);

            GridPane.setHgrow(cb, Priority.NEVER);
            GridPane.setHgrow(dateLabel, Priority.ALWAYS);
            GridPane.setHgrow(nameLabel, Priority.ALWAYS);
            GridPane.setHgrow(downloadBtn, Priority.NEVER);

            checkBoxes.add(cb);
            reports.add(report);
            row++;
        }
    }

    /**
     * Создаёт заголовок таблицы для документов или отчётов.
     *
     * @param isReport {@code true} если отображаются отчёты
     */
    private void addTableHeader(boolean isReport) {
        tableGrid.getChildren().clear();
        String[] headers = isReport ?
                new String[]{"", I18n.t("docs.header.date"), I18n.t("docs.header.name"), I18n.t("docs.header.download")}
                : new String[]{"", I18n.t("docs.header.date"), I18n.t("docs.header.document"), I18n.t("docs.header.download")};
        for (int col = 0; col < headers.length; col++) {
            Label header = new Label(headers[col]);
            header.getStyleClass().add("table-header");
            if (col == 0) header.getStyleClass().add("col-checkbox");
            if (col == headers.length - 1) header.getStyleClass().addAll("col-download", "table-cell-last");
            header.setMaxWidth(Double.MAX_VALUE);
            header.setAlignment(javafx.geometry.Pos.CENTER);
            tableGrid.add(header, col, 0);
            GridPane.setHgrow(header, Priority.ALWAYS);
        }
    }

    /**
     * Загружает новый PDF-документ и обновляет список.
     */
    private void onAdd() {
        if (!I18n.t("docs.type.documents").equals(typeComboBox.getValue())) {
            showError(statusLabel, "docs.add_only_documents");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle(I18n.t("common.choose_file"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showOpenDialog(tableGrid.getScene().getWindow());
        if (file != null) {

            runAsync(() -> {
                try {
                    documentService.uploadDocument(file);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "docs.document_uploaded");
                    loadDocuments();
                });
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.upload_error") + ex.getMessage())));
        }
    }

    /**
     * Удаляет выбранные элементы таблицы.
     */
    private void onDelete() {
        List<Integer> indicesToDelete = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                indicesToDelete.add(i);
            }
        }
        if (indicesToDelete.isEmpty()) {
            showError(statusLabel, "docs.select_items_delete");
            return;
        }

        if (I18n.t("docs.type.documents").equals(typeComboBox.getValue())) {
            runAsync(() -> {
                for (int idx : indicesToDelete) {
                    DocumentDto doc = documents.get(idx);
                    try {
                        documentService.deleteDocument(doc.id());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "docs.documents_deleted");
                    loadDocuments();
                });
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.delete_error") + ex.getMessage())));
        } else {
            runAsync(() -> {
                for (int idx : indicesToDelete) {
                    ReportListItemDto report = reports.get(idx);
                    try {
                        reportService.deleteReport(report.id());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "docs.report_deleted");
                    loadReports();
                });
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.delete_error") + ex.getMessage())));
        }
    }

    /**
     * Сохраняет выбранный документ в файл.
     *
     * @param docId   идентификатор документа
     * @param docName предложенное имя файла
     */
    private void onDownloadDocument(long docId, String docName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(I18n.t("docs.save_as"));
        chooser.setInitialFileName(docName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File saveFile = chooser.showSaveDialog(tableGrid.getScene().getWindow());
        if (saveFile != null) {
            runAsync(() -> {
                try (InputStream in = documentService.downloadDocument(docId);
                     FileOutputStream out = new FileOutputStream(saveFile)) {
                    in.transferTo(out);
                    Platform.runLater(() -> showSuccess(statusLabel, "docs.document_downloaded"));
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.download_error") + ex.getMessage())));
        }
    }

    /**
     * Сохраняет выбранный отчёт в файл.
     *
     * @param reportId   идентификатор отчёта
     * @param reportName имя файла для сохранения
     */
    private void onDownloadReport(long reportId, String reportName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(I18n.t("docs.save_as"));
        chooser.setInitialFileName(reportName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File saveFile = chooser.showSaveDialog(tableGrid.getScene().getWindow());
        if (saveFile != null) {
            runAsync(() -> {
                try (FileOutputStream out = new FileOutputStream(saveFile)) {
                    byte[] data = reportService.downloadReport(reportId);
                    out.write(data);
                    Platform.runLater(() -> showSuccess(statusLabel, "docs.report_downloaded"));
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }, ex -> Platform.runLater(() -> showError(statusLabel, I18n.t("docs.download_error") + ex.getMessage())));
        }
    }

    /**
     * Запрашивает извлечение параметров из выбранного документа и
     * открывает экран модели с новыми значениями.
     */
    private void onSelect() {
        int selectedIdx = -1;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                if (selectedIdx != -1) {
                    showError(statusLabel, "docs.select_one");
                    return;
                }
                selectedIdx = i;
            }
        }
        if (selectedIdx == -1) {
            showError(statusLabel, "docs.select_for_extract");
            return;
        }
        DocumentDto selectedDoc = documents.get(selectedIdx);
        if (modelId == null) {
            showError(statusLabel, "docs.unknown_model");
            return;
        }
        selectButton.setDisable(true);
        runAsync(() -> {
            try {
                LlmParameterExtractionRequestDto req = new LlmParameterExtractionRequestDto(modelId, selectedDoc.id());
                LlmParameterExtractionResponseDto resp = LlmService.getInstance().extractParameters(
                        org.example.economicssimulatorclient.util.SessionManager.getInstance().getBaseUri(),
                        req
                );
                List<org.example.economicssimulatorclient.dto.ModelParameterDto> newParams = resp.parameters();
                Platform.runLater(() -> SceneManager.switchTo("model_view.fxml", c -> {
                    ((ModelViewController) c).initWithModelId(modelId, newParams);
                }));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                Platform.runLater(() -> selectButton.setDisable(false));
            }
        }, ex -> Platform.runLater(() -> {
            showError(statusLabel, I18n.t("docs.extraction_error") + ex.getMessage());
            selectButton.setDisable(false);
        }));
    }

    /**
     * Очищает выбранные флажки и сообщение статуса.
     */
    @Override
    public void clearFields() {
        checkBoxes.forEach(cb -> cb.setSelected(false));
        clearStatusLabel();
    }
}
