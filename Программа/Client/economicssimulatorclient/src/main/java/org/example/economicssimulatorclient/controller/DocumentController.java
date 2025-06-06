package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.service.DocumentService;
import org.example.economicssimulatorclient.util.SceneManager;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DocumentController extends BaseController {

    @FXML
    private Button backButton;

    @FXML
    private Button mainButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button selectButton;

    @FXML
    private GridPane tableGrid;

    @FXML
    protected Label statusLabel; // уже в BaseController

    private final DocumentService documentService = new DocumentService();

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<DocumentDto> documents = new ArrayList<>();

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml", MainController::loadModelList));

        loadDocuments();

        addButton.setOnAction(e -> onAdd());
        deleteButton.setOnAction(e -> onDelete());
        selectButton.setOnAction(e -> onSelect());
    }

    private void loadDocuments() {
        tableGrid.getChildren().clear();
        checkBoxes.clear();
        documents.clear();
        clearStatusLabel();

        addTableHeader();

        runAsync(() -> {
            List<DocumentDto> docs = null;
            try {
                docs = documentService.getUserDocuments();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<DocumentDto> finalDocs = docs;
            Platform.runLater(() -> fillTable(finalDocs));
        }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка загрузки документов: " + ex.getMessage())));
    }

    private void fillTable(List<DocumentDto> docs) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        int row = 1;
        for (DocumentDto doc : docs) {
            CheckBox cb = new CheckBox();
            checkBoxes.add(cb);

            Label dateLabel = new Label(doc.uploadedAt() != null ? doc.uploadedAt().format(fmt) : "");
            Label nameLabel = new Label(doc.name());
            Button downloadBtn = new Button("⤓");
            downloadBtn.getStyleClass().add("download-button");

            final long docId = doc.id();
            final String docName = doc.name();

            downloadBtn.setOnAction(ev -> onDownload(docId, docName));

            tableGrid.add(cb,      0, row);
            tableGrid.add(dateLabel, 1, row);
            tableGrid.add(nameLabel, 2, row);
            tableGrid.add(downloadBtn, 3, row);

            documents.add(doc);
            row++;
        }
    }

    private void addTableHeader() {
        for (int col = 0; col < 4; col++) {
            Label header = new Label(headers[col]);
            header.getStyleClass().add("table-header");
            tableGrid.add(header, col, 0);
        }
// Добавить псевдо-линию:
        Rectangle line = new Rectangle();
        line.setHeight(2);
        line.setFill(Color.web("#b2b1b1"));
        GridPane.setColumnSpan(line, 4);
        tableGrid.add(line, 0, 1);
        tableGrid.add(new Label(""),                  0, 0);
        Label dateHeader = new Label("Дата");
        dateHeader.getStyleClass().add("table-header");
        tableGrid.add(dateHeader, 1, 0);
        Label nameHeader = new Label("Документ");
        nameHeader.getStyleClass().add("table-header");
        tableGrid.add(nameHeader, 2, 0);
        Label downloadHeader = new Label("Скачать");
        downloadHeader.getStyleClass().add("table-header");
        tableGrid.add(downloadHeader, 3, 0);
    }

    private void onAdd() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showOpenDialog(tableGrid.getScene().getWindow());
        if (file != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите описание");
            dialog.getEditor().setText("");
            dialog.showAndWait();
            String description = dialog.getResult();

            runAsync(() -> {
                try {
                    documentService.uploadDocument(file, description);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "Документ загружен");
                    loadDocuments();
                });
            }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка загрузки: " + ex.getMessage())));
        }
    }

    private void onDelete() {
        List<Integer> indicesToDelete = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                indicesToDelete.add(i);
            }
        }
        if (indicesToDelete.isEmpty()) {
            showError(statusLabel, "Выбран для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, tr("documents.confirmDelete"), ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Подтвердите удаление");
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;

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
                showSuccess(statusLabel, "Документ удален");
                loadDocuments();
            });
        }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка удаления: " + ex.getMessage())));
    }

    private void onDownload(long docId, String docName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить как");
        chooser.setInitialFileName(docName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File saveFile = chooser.showSaveDialog(tableGrid.getScene().getWindow());
        if (saveFile != null) {
            runAsync(() -> {
                try (InputStream in = documentService.downloadDocument(docId);
                     FileOutputStream out = new FileOutputStream(saveFile)) {
                    try {
                        in.transferTo(out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> showSuccess(statusLabel, "Документ скачан"));
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }, ex -> Platform.runLater(() -> showError(statusLabel, "Ошибка скачивания: " + ex.getMessage())));
        }
    }

    private void onSelect() {
        List<DocumentDto> selected = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selected.add(documents.get(i));
            }
        }
        if (selected.isEmpty()) {
            showError(statusLabel, "Выберите хотя бы 1");
            return;
        }
        String names = selected.stream().map(DocumentDto::name).reduce((a, b) -> a + ", " + b).orElse("");
        showSuccess(statusLabel, "Выбраны: " + names); // или tr("documents.selected") + names;
    }

    @Override
    public void clearFields() {
        checkBoxes.forEach(cb -> cb.setSelected(false));
        clearStatusLabel();
    }
}
