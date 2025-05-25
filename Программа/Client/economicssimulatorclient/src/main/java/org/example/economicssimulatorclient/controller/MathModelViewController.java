package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.dto.ModelParameterDto;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.List;

public class MathModelViewController {

    @FXML
    private Button backButton;

    @FXML
    private Button mainButton;

    @FXML
    private VBox paramsVBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField typeField;

    @FXML
    private TextArea formulaArea;

    @FXML
    private Button runButton;

    @FXML
    private Button addDocButton;

    @FXML
    private Button editButton;

    private MathModelDto model;

    /**
     * Устанавливаем отображаемую модель (вызывается из предыдущего экрана)
     */
    public void setMathModel(MathModelDto model) {
        this.model = model;
        fillModelInfo();
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> SceneManager.back());
        mainButton.setOnAction(e -> SceneManager.switchTo("main.fxml"));

        runButton.setOnAction(e -> {
            // Переход к экрану запуска симуляции
            SceneManager.switchToWithController("math_model_start.fxml", ctrl -> {
                if (ctrl instanceof MathModelStartController c) {
                    c.setMathModel(model);
                }
            });
        });

        addDocButton.setOnAction(e -> {
            // Пока не реализуем
        });

        editButton.setOnAction(e -> {
            // Переход к экрану редактирования
            SceneManager.switchToWithController("create_math_model.fxml", ctrl -> {
                if (ctrl instanceof CreateMathModelController c) {
                    c.setEditModel(model);
                }
            });
        });
    }

    private void fillModelInfo() {
        if (model == null) return;
        nameField.setText(model.name());
        typeField.setText(model.type());
        formulaArea.setText(model.formula());

        paramsVBox.getChildren().clear();
        List<ModelParameterDto> params = model.parameters();
        if (params != null) {
            for (ModelParameterDto param : params) {
                VBox card = new VBox(8);
                card.getStyleClass().add("param-card");
                TextField name = new TextField(param.name());
                name.setEditable(false);
                name.setPromptText("Обозначение");
                name.getStyleClass().add("text-field");
                TextField desc = new TextField(param.description());
                desc.setEditable(false);
                desc.setPromptText("Описание");
                desc.getStyleClass().add("text-field");
                TextField type = new TextField(param.type());
                type.setEditable(false);
                type.setPromptText("Тип");
                type.getStyleClass().add("text-field");
                card.getChildren().addAll(name, desc, type);
                paramsVBox.getChildren().add(card);
            }
        }
    }
}
