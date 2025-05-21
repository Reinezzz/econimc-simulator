package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseControllerTest {

    private BaseController controller;

    @BeforeEach
    void setUp() {
        controller = new BaseController() {};
    }

    @Test
    void testShowError_setsErrorStyle() {
        Label label = new Label();
        controller.showError(label, "Test error");
        assertEquals("-fx-text-fill: #e74c3c;", label.getStyle());
    }

    @Test
    void testShowSuccess_setsSuccessStyle() {
        Label label = new Label();
        controller.showSuccess(label, "Test ok");
        assertEquals("-fx-text-fill: #27ae60;", label.getStyle());
    }

    @Test
    void testProvideAndGetSingleton() {
        Object service = new Object();
        BaseController.provide(Object.class, service);
        assertSame(service, BaseController.get(Object.class));
    }

    @Test
    void testGetSingleton_noInstance_throws() {
        assertThrows(IllegalStateException.class, () -> BaseController.get(String.class));
    }
}
