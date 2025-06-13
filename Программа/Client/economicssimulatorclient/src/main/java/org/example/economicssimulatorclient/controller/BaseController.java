package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.economicssimulatorclient.service.LanguageService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;

import java.util.Locale;

/**
 * Базовый класс всех JavaFX контроллеров. Содержит вспомогательные методы
 * для вывода сообщений, запуска фоновых задач и переключения языка.
 */
public abstract class BaseController {
    @FXML
    protected Label statusLabel;

    @FXML
    protected Button langButton;

    /**
     * Переводит указанный ключ локализации.
     *
     * @param key ключ в ресурсах сообщений
     * @return локализованный текст либо сам ключ, если перевода нет
     */
    protected String tr(String key) {
        return I18n.t(key);
    }

    /**
     * Выводит сообщение об ошибке в красном цвете.
     *
     * @param label    метка для вывода
     * @param msgOrKey текст либо ключ локализации
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
     * Показывает положительный статус зелёным цветом.
     *
     * @param label    метка для вывода
     * @param msgOrKey текст либо ключ локализации
     */
    protected void showSuccess(Label label, String msgOrKey) {
        if (label == null) return;
        String text = I18n.t(msgOrKey);
        label.setText(!text.startsWith("!") ? text : msgOrKey);
        label.setStyle("-fx-text-fill: #27ae60;");
    }

    /**
     * Выполняет задачу в отдельном потоке.
     *
     * @param task    код для выполнения
     * @param onError обработчик возможной ошибки
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


    private static final java.util.Map<Class<?>, Object> SINGLETONS = new java.util.HashMap<>();

    /**
     * Сохраняет экземпляр в реестре одиночек.
     *
     * @param type     тип класса
     * @param instance экземпляр для сохранения
     */
    public static <T> void provide(Class<T> type, T instance) {
        SINGLETONS.put(type, instance);
    }

    /**
     * Возвращает ранее сохранённый экземпляр.
     *
     * @param type запрашиваемый тип
     * @return экземпляр указанного типа
     * @throws IllegalStateException если экземпляр не был сохранён
     */
    public static <T> T get(Class<T> type) {
        Object inst = SINGLETONS.get(type);
        if (inst == null)
            throw new IllegalStateException("No singleton provided for " + type);
        return type.cast(inst);
    }

    /**
     * Стирает текст в статусной метке, если она присутствует.
     */
    public void clearStatusLabel() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }

    /**
     * Очищает все поля формы. Реализация предоставляется наследниками.
     */
    public abstract void clearFields();


    /**
     * Возвращает часть строки в зависимости от текущей локали.
     * Ожидается формат "русский^english".
     *
     * @param line строка с двумя вариантами текста
     * @return выбранная локализованная часть
     */
    public static String localizedValue(String line) {
        String[] parts = line.split("\\^", 2);
        if (parts.length == 1) return line;
        String lang = I18n.getLocale().getLanguage();
        if (lang.equals("ru")) return parts[0].trim();
        else return parts[1].trim();
    }

    /**
     * Подготавливает кнопку смены языка и назначает обработчик.
     */
    protected void initLangButton() {
        if (langButton != null) {
            updateLangButtonText();
            langButton.setOnAction(e -> onLangButtonClicked());
        }
    }

    /**
     * Обновляет текст на кнопке языка в соответствии с выбранной локалью.
     */
    private void updateLangButtonText() {
        if (langButton != null) {
            String text = I18n.getLocale().getLanguage().equals("ru") ? "EN" : "RU";
            langButton.setText(text);
        }
    }

    /**
     * Обработчик нажатия на кнопку смены языка. Изменяет текущую локаль,
     * сохраняет выбор через {@link LanguageService} и перезагружает сцену.
     */
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
