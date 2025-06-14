package org.example.economicssimulatorclient.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.controller.ModelViewController;
import org.example.economicssimulatorclient.dto.ModelParameterDto;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelViewControllerTest {
    ModelViewController ctrl;

    @BeforeEach
    void setup() {
        ctrl = new ModelViewController();
        ctrl.parameterList = new VBox();
        ctrl.statusLabel = new Label();
        ctrl.valueFields.clear();
        ctrl.valueFields.add(new TextField("1"));
        ctrl.parameters = List.of(new ModelParameterDto(1L,1L,"x","double","old","","",0));
    }

    @Test
    void fillParametersFromExtraction_updatesValues() {
        ctrl.fillParametersFromExtraction(List.of(new ModelParameterDto(1L,1L,"x","double","new","","",0)));
        assertEquals("new", ctrl.parameters.get(0).paramValue());
    }

    @Test
    void clearFields_clearsChatIfNotNull() {
        ctrl.llmChatComponent = new org.example.economicssimulatorclient.controller.LlmChatComponentController();
        ctrl.llmChatComponent.chatArea = new VBox();
        ctrl.llmChatComponent.chatArea.getChildren().add(new Label("abc"));
        ctrl.clearFields();
        assertEquals(0, ctrl.llmChatComponent.chatArea.getChildren().size());
    }

    @Test
    void toggleEditMode_enablesEdit() {
        ctrl.editMode = false;
        ctrl.valueFields.add(new TextField("val"));
        ctrl.toggleEditMode();
        assertTrue(ctrl.valueFields.get(1).isEditable());
    }
}
