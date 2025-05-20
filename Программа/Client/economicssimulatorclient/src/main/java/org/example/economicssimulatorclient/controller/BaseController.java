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

    // ====== DI механика (по-простому, без внешних либ) ======

    private static final java.util.Map<Class<?>, Object> SINGLETONS = new java.util.HashMap<>();

    /**
     * Зарегистрировать singleton сервис
     */
    public static <T> void provide(Class<T> type, T instance) {
        SINGLETONS.put(type, instance);
    }

    /**
     * Получить singleton сервис
     */
    public static <T> T get(Class<T> type) {
        Object inst = SINGLETONS.get(type);
        if (inst == null)
            throw new IllegalStateException("No singleton provided for " + type);
        return type.cast(inst);
    }

}
