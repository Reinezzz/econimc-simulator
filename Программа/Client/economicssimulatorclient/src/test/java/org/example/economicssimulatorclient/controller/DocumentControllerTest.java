package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.economicssimulatorclient.controller.DocumentController;
import org.example.economicssimulatorclient.dto.DocumentDto;
import org.example.economicssimulatorclient.dto.ReportListItemDto;
import org.example.economicssimulatorclient.service.DocumentService;
import org.example.economicssimulatorclient.service.ReportService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentControllerTest {

    @Mock DocumentService documentService;
    @Mock ReportService reportService;

    DocumentController ctrl;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ctrl = new DocumentController();

        // Подменяем сервисы через reflection
        Field docServiceField = DocumentController.class.getDeclaredField("documentService");
        docServiceField.setAccessible(true);
        docServiceField.set(ctrl, documentService);

        Field repServiceField = DocumentController.class.getDeclaredField("reportService");
        repServiceField.setAccessible(true);
        repServiceField.set(ctrl, reportService);

        ctrl.statusLabel = new Label();
        ctrl.tableGrid = new GridPane();
        ctrl.backButton = new Button();
        ctrl.mainButton = new Button();
        ctrl.addButton = new Button();
        ctrl.deleteButton = new Button();
        ctrl.selectButton = new Button();
        ctrl.typeComboBox = new javafx.scene.control.ComboBox<>();
    }

    @Test
    void fromView_setsModelIdAndVisibility() {
        ctrl.fromView(true);
        assertTrue(ctrl.selectButton.isVisible());
    }

    @Test
    void clearFields_unchecksAllBoxes() {
        javafx.scene.control.CheckBox cb = new javafx.scene.control.CheckBox();
        cb.setSelected(true);
        ctrl.checkBoxes.add(cb);
        ctrl.clearFields();
        assertFalse(cb.isSelected());
    }

    @Test
    void fillDocumentsTable_addsCheckBoxesAndDocs() {
        DocumentDto doc = new DocumentDto(1L, 1L, "file.pdf", "/somewhere", LocalDateTime.now());
        ctrl.fillDocumentsTable(List.of(doc));
        assertEquals(1, ctrl.checkBoxes.size());
        assertEquals(1, ctrl.documents.size());
    }
}
