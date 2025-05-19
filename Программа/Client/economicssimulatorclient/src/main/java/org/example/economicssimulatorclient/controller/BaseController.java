package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import org.example.economicssimulatorclient.util.I18n;

public abstract class BaseController {

    /** Получить строку локализации по ключу */
    protected String tr(String key) {
        return I18n.t(key);
    }

    protected void showError(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        // Если нашли ключ локализации, используем перевод
        if (!text.startsWith("!")) {
            label.setText(text);
        } else {
            // Если ключа нет — просто выводим raw-текст (прилетел с сервера)
            label.setText(msgOrKey);
        }
        label.setStyle("-fx-text-fill: #e74c3c;");
    }

    /** Показывает сообщение об успехе. */
    protected void showSuccess(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        label.setText(!text.startsWith("!") ? text : msgOrKey);
        label.setStyle("-fx-text-fill: #27ae60;");
    }
}
