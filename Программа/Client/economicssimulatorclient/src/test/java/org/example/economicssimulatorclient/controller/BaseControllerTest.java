package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class BaseControllerTest {

    static class TestController extends BaseController {
        @Override public void clearFields() {}
    }

    @Test
    void provideAndGet_works() {
        TestController ctrl = new TestController();
        BaseController.provide(TestController.class, ctrl);
        assertSame(ctrl, BaseController.get(TestController.class));
    }

    @Test
    void localizedValue_ru() {
        org.example.economicssimulatorclient.util.I18n.setLocale(new java.util.Locale("ru"));
        assertEquals("Рус", BaseController.localizedValue("Рус^Eng"));
    }

    @Test
    void localizedValue_en() {
        org.example.economicssimulatorclient.util.I18n.setLocale(java.util.Locale.ENGLISH);
        assertEquals("Eng", BaseController.localizedValue("Рус^Eng"));
    }

    @Test
    void showError_setsRedText() {
        TestController ctrl = new TestController();
        Label lbl = new Label();
        ctrl.showError(lbl, "no_such_key");
        assertTrue(lbl.getStyle().contains("#e74c3c"));
    }

    @Test
    void showSuccess_setsGreenText() {
        TestController ctrl = new TestController();
        Label lbl = new Label();
        ctrl.showSuccess(lbl, "no_such_key");
        assertTrue(lbl.getStyle().contains("#27ae60"));
    }
}
