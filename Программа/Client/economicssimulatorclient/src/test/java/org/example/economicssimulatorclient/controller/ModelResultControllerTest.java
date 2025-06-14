package org.example.economicssimulatorclient.controller;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.controller.ModelResultController;
import org.example.economicssimulatorclient.dto.CalculationResponseDto;
import org.example.economicssimulatorclient.dto.ModelParameterDto;
import org.example.economicssimulatorclient.dto.ModelResultDto;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ModelResultControllerTest {
    ModelResultController ctrl;

    @BeforeEach
    void setup() {
        ctrl = new ModelResultController();
        ctrl.parameterList = new VBox();
        ctrl.resultArea = new TextArea();
        ctrl.chartPane = new Pane();
        ctrl.typeComboBox = new ComboBox<>();
        ctrl.saveButton = new Button();
        ctrl.statusLabel = new Label();
        ctrl.chat = new VBox();
    }

    @Test
    void validateParameters_detectsEmpty() {
        ctrl.parameters = List.of(new ModelParameterDto(1L,1L,"p","double","","","",0));
        assertFalse(ctrl.validateParameters());
    }

    @Test
    void buildVisualizations_returnsList() {
        ctrl.chartDataMap = Map.of("abc", Map.of("val", 2));
        assertFalse(ctrl.buildVisualizations().isEmpty());
    }

    @Test
    void clearFields_resetsEverything() {
        ctrl.resultArea.setText("test");
        ctrl.parameterList.getChildren().add(new Label("param"));
        ctrl.clearFields();
        assertEquals("", ctrl.resultArea.getText());
        assertEquals(0, ctrl.parameterList.getChildren().size());
    }
}
