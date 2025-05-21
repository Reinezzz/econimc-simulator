package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import org.example.economicssimulatorclient.util.I18n;

/**
 * Базовый класс для всех JavaFX-контроллеров.
 * Предоставляет утилиты для локализации, DI и обработки ошибок.
 */
public abstract class BaseController {

    /**
     * Получить строку локализации по ключу.
     * @param key ключ локализации
     * @return строка перевода или ключ с !, если нет перевода
     */
    protected String tr(String key) {
        return I18n.t(key);
    }

    /**
     * Показывает ошибку с учетом локализации.
     * @param label Label для вывода ошибки
     * @param msgOrKey ключ локализации или текст ошибки
     */
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

    /**
     * Показывает сообщение об успехе.
     * @param label Label для вывода
     * @param msgOrKey ключ локализации или текст
     */
    protected void showSuccess(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        label.setText(!text.startsWith("!") ? text : msgOrKey);
        label.setStyle("-fx-text-fill: #27ae60;");
    }

    /**
     * Запустить задачу асинхронно в отдельном потоке.
     * @param task задача
     * @param onError обработчик ошибок
     */
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

    // ====== DI механика (Singleton сервисы без внешних либ) ======

    private static final java.util.Map<Class<?>, Object> SINGLETONS = new java.util.HashMap<>();

    /**
     * Зарегистрировать singleton сервис.
     * @param type тип класса
     * @param instance объект-синглтон
     * @param <T> тип сервиса
     */
    public static <T> void provide(Class<T> type, T instance) {
        SINGLETONS.put(type, instance);
    }

    /**
     * Получить singleton сервис.
     * @param type тип класса
     * @param <T> тип возвращаемого объекта
     * @return объект-синглтон
     * @throws IllegalStateException если сервис не зарегистрирован
     */
    public static <T> T get(Class<T> type) {
        Object inst = SINGLETONS.get(type);
        if (inst == null)
            throw new IllegalStateException("No singleton provided for " + type);
        return type.cast(inst);
    }
}
