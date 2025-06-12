package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.economicssimulatorclient.service.LanguageService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.Locale;

public abstract class BaseController {
    @FXML
    protected Label statusLabel;

    @FXML
    protected Button langButton;

    protected String tr(String key) {
        return I18n.t(key);
    }

    protected void showError(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        if (!text.startsWith("!")) {
            label.setText(text);
        } else {
            label.setText(msgOrKey);
        }
        label.setStyle("-fx-text-fill: #e74c3c;");
    }

    protected void showSuccess(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        label.setText(!text.startsWith("!") ? text : msgOrKey);
        label.setStyle("-fx-text-fill: #27ae60;");
    }

    protected void runAsync(Runnable task, java.util.function.Consumer<Throwable> onError) {
        Thread t = new Thread(() -> {
            try {
                task.run();
            } catch (Throwable ex) {
                if (onError != null) onError.accept(ex);
            }
        }, "fx-bg");
        t.setDaemon(true);
        t.start();
    }


    private static final java.util.Map<Class<?>, Object> SINGLETONS = new java.util.HashMap<>();

    public static <T> void provide(Class<T> type, T instance) {
        SINGLETONS.put(type, instance);
    }

    public static <T> T get(Class<T> type) {
        Object inst = SINGLETONS.get(type);
        if (inst == null)
            throw new IllegalStateException("No singleton provided for " + type);
        return type.cast(inst);
    }

    public void clearStatusLabel() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }

    public abstract void clearFields();

    public static String localizedValue(String line) {
        String[] parts = line.split("\\^", 2);
        if (parts.length == 1) return line;
        String lang = I18n.getLocale().getLanguage();
        if (lang.equals("ru")) return parts[0].trim();
        else return parts[1].trim();
    }

    protected void initLangButton() {
        if (langButton != null) {
            updateLangButtonText();
            langButton.setOnAction(e -> onLangButtonClicked());
        }
    }

    private void updateLangButtonText() {
        if (langButton != null) {
            String text = I18n.getLocale().getLanguage().equals("ru") ? "EN" : "RU";
            langButton.setText(text);
        }
    }

    @FXML
    protected void onLangButtonClicked() {
        Locale newLocale = I18n.getLocale().getLanguage().equals("ru") ? Locale.ENGLISH : new Locale("ru");
        I18n.setLocale(newLocale);
        try {
            new LanguageService().updateLanguage(newLocale.getLanguage());
        } catch (Exception ignored) {
        }
        SceneManager.reloadCurrent();
    }


}
