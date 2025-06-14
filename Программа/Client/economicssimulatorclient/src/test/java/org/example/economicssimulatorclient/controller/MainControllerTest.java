package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.controller.MainController;
import org.example.economicssimulatorclient.dto.EconomicModelDto;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainControllerTest {
    @Mock EconomicModelService modelService;

    MainController ctrl;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ctrl = new MainController();

        Field msField = MainController.class.getDeclaredField("modelService");
        msField.setAccessible(true);
        msField.set(ctrl, modelService);

        ctrl.statusLabel = new Label();
        ctrl.modelList = new VBox();
        ctrl.tileButton = new VBox();
        ctrl.tileDocuments = new VBox();
    }

    @Test
    void clearFields_clearsStatusLabel() {
        ctrl.statusLabel.setText("Error!");
        ctrl.clearFields();
        assertEquals("", ctrl.statusLabel.getText());
    }

    @Test
    void populateModelList_addsButtons() {
        EconomicModelDto dto = new EconomicModelDto(1L, "type", "name", "desc", null, null, "c", "u", "f");
        ctrl.populateModelList(List.of(dto));
        assertEquals(1, ctrl.modelList.getChildren().size());
    }

    @Test
    void loadModelList_invokesService() throws IOException, InterruptedException {
        when(modelService.getAllModels()).thenReturn(List.of());
        ctrl.loadModelList();
        verify(modelService, atLeastOnce()).getAllModels();
    }
}
