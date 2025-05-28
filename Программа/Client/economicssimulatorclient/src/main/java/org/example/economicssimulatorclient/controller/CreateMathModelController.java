package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.MathModelService;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.*;
import java.util.stream.Collectors;

public class CreateMathModelController {

    @FXML private Button backButton;
    @FXML private Button mainButton;
    @FXML private Button saveButton;
    @FXML private Button addParamButton;

    @FXML private VBox paramsVBox;
    @FXML private ScrollPane paramsScroll;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextArea formulaArea;

    private final MathModelService mathModelService = new MathModelService();

    // Если редактируем, сюда придёт существующая модель
    private MathModelDto editModel = null;

    // Для соответствия Enum (отображение <-> значение)
    private static final LinkedHashMap<String, String> typeMap = new LinkedHashMap<>();
    static {
        typeMap.put("Линейное уравнение", "ALGEBRAIC_EQUATION");
        typeMap.put("Оптимизационная модель", "OPTIMIZATION");
        typeMap.put("Множественная линейная регрессия", "REGRESSION");
        typeMap.put("Система уравнений", "SYSTEM_OF_EQUATIONS");
    }

    @FXML
    private void initialize() {
        // Заполняем ComboBox типами
        typeComboBox.getItems().addAll(typeMap.keySet());

        backButton.setOnAction(e -> SceneManager.back());
        mainButton.setOnAction(e -> SceneManager.switchToWithController("main.fxml", MainController::loadModels));
        addParamButton.setOnAction(e -> addParamCard(null,null, null, null));
        saveButton.setOnAction(e -> onSave());

        // если это создание, поля оставляем пустыми
        if (editModel != null) {
            fillForEdit();
        }
    }

    /** Для передачи модели в режим редактирования */
    public void setEditModel(MathModelDto model) {
        this.editModel = model;
        if (model != null && typeComboBox != null) {
            fillForEdit();
        }
    }


    /** Заполнить форму для редактирования */
    private void fillForEdit() {
        nameField.setText(editModel.name());
        formulaArea.setText(editModel.formula());
        String displayType = typeMap.entrySet().stream()
                .filter(e -> e.getValue().equals(editModel.type()))
                .map(Map.Entry::getKey).findFirst().orElse(null);
        typeComboBox.setValue(displayType);

        paramsVBox.getChildren().clear();
        if (editModel.parameters() != null) {
            for (ModelParameterDto param : editModel.parameters()) {
                addParamCard(param.id(), param.name(), param.description(), param.paramType());
            }
        }
    }

    /** Добавить пустую карточку параметра (или заполненную для редактирования) */
    private void addParamCard(Long id, String name, String desc, String type) {
        HBox card = new HBox(8);

        TextField nameField = new TextField(name == null ? "" : name);
        nameField.setPromptText("Обозначение");
        TextField descField = new TextField(desc == null ? "" : desc);
        descField.setPromptText("Описание");
        TextField typeField = new TextField(type == null ? "" : type);
        typeField.setPromptText("Тип");

        card.setUserData(id);

        Button removeBtn = new Button("✕");
        removeBtn.setOnAction(e -> paramsVBox.getChildren().remove(card));
        card.getChildren().addAll(nameField, descField, typeField, removeBtn);
        paramsVBox.getChildren().add(card);
    }

    /** Обработка кнопки "Сохранить" с валидацией */
    private void onSave() {
        // Валидация полей модели
        boolean valid = true;
        StringBuilder errMsg = new StringBuilder();

        String name = nameField.getText().trim();
        String formula = formulaArea.getText().trim();
        String displayType = typeComboBox.getValue();
        String modelType = displayType == null ? null : typeMap.get(displayType);

        if (name.isEmpty()) {
            valid = false;
            errMsg.append("Заполните поле 'Название'.\n");
            highlight(nameField);
        } else resetHighlight(nameField);

        if (modelType == null) {
            valid = false;
            errMsg.append("Выберите тип модели.\n");
            highlight(typeComboBox);
        } else resetHighlight(typeComboBox);

        if (formula.isEmpty()) {
            valid = false;
            errMsg.append("Заполните поле 'Формула'.\n");
            highlight(formulaArea);
        } else resetHighlight(formulaArea);

        // Валидация параметров
        List<ModelParameterCreateDto> params = new ArrayList<>();
        boolean paramsValid = true;
        for (javafx.scene.Node node : paramsVBox.getChildren()) {
            if (node instanceof HBox card) {
                TextField n = (TextField) card.getChildren().get(0);
                TextField d = (TextField) card.getChildren().get(1);
                TextField t = (TextField) card.getChildren().get(2);
                boolean ok = true;
                if (n.getText().trim().isEmpty()) {
                    ok = false;
                    highlight(n);
                } else resetHighlight(n);
                if (d.getText().trim().isEmpty()) {
                    ok = false;
                    highlight(d);
                } else resetHighlight(d);
                if (t.getText().trim().isEmpty()) {
                    ok = false;
                    highlight(t);
                } else resetHighlight(t);

                if (!ok) paramsValid = false;
                params.add(new ModelParameterCreateDto(
                        n.getText().trim(),
                        d.getText().trim(),
                        t.getText().trim(),
                        null
                ));
            }
        }
        if (params.isEmpty() || !paramsValid) {
            valid = false;
            errMsg.append("Вы не заполнили параметры.\n");
        }

        if (!valid) {
            showAlert("Ошибка заполнения", errMsg.toString());
            return;
        }

        // Формируем DTO и сохраняем
        try {
            if (editModel == null) { // создание
                MathModelCreateDto dto = new MathModelCreateDto(
                        name, formula, modelType, params
                );
                mathModelService.createModel(dto);
            } else { // обновление
                List<ModelParameterUpdateDto> paramDtos = new ArrayList<>();
                for (javafx.scene.Node node : paramsVBox.getChildren()) {
                    if (node instanceof HBox card) {
                        TextField n = (TextField) card.getChildren().get(0);
                        TextField d = (TextField) card.getChildren().get(1);
                        TextField t = (TextField) card.getChildren().get(2);
                        Long id = (Long) card.getUserData();
                        paramDtos.add(new ModelParameterUpdateDto(
                                id, n.getText().trim(), t.getText().trim(), d.getText().trim(), null
                        ));
                    }
                }
                MathModelUpdateDto dto = new MathModelUpdateDto(
                        editModel.id(),
                        name, formula, modelType,
                        paramDtos
                );
                mathModelService.updateModel(dto);
            }
            Platform.runLater(() ->
                    // <-- обязательно!
                    SceneManager.switchToWithController("main.fxml", MainController::loadModels)
            );

        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось сохранить модель:\n" + e.getMessage());
        }
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
