package org.example.economicssimulatorclient;

import javafx.application.Platform;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.util.SessionManager;

/**
 * Основной класс клиентского приложения «Экономический симулятор». Здесь
 * выполняется инициализация JavaFX и выбор начальной сцены. FXML‑файлы
 * располагаются в каталоге {@code /org/example/economicssimulatorclient/} и
 * загружаются через {@link SceneManager}, который также кэширует созданные
 * сцены.
 */
public class MainApp extends Application {

    /**
     * Вызывается средой JavaFX после старта из {@link #main(String[])}. Здесь
     * регистрируются необходимые сервисы, инициализируется {@link SceneManager}
     * на основе переданной сцены и выполняется проверка токена аутентификации,
     * чтобы определить, какой FXML открыть.
     *
     * @param primaryStage основной {@link Stage}, созданный JavaFX
     * @throws Exception если произошла ошибка инициализации
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BaseController.provide(AuthService.class, new AuthService());
        SceneManager.init(primaryStage);
        String refreshToken = SessionManager.getInstance().getRefreshToken();
        boolean justLoggedOut = SessionManager.getInstance().isJustLoggedOut();
        if (refreshToken != null && !justLoggedOut) {
            new Thread(() -> {
                boolean refreshed = false;
                try {
                    refreshed = AuthService.getInstance().refreshTokens();
                } catch (Exception ex) {
                }
                boolean finalRefreshed = refreshed;
                Platform.runLater(() -> {
                    if (finalRefreshed) {
                        BaseController.provide(EconomicModelService.class, new EconomicModelService());
                        SceneManager.switchToWithoutContorller("main.fxml");
                    } else {
                        SceneManager.switchTo("authorization.fxml", c -> {
                            ((BaseController) c).clearStatusLabel();
                            ((BaseController) c).clearFields();
                        });
                    }
                });
            }).start();
        } else {
            SceneManager.switchTo("authorization.fxml", c -> {
                ((BaseController) c).clearStatusLabel();
                ((BaseController) c).clearFields();
            });
        }

        primaryStage.setTitle(org.example.economicssimulatorclient.util.I18n.t("app.title"));
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Традиционная точка входа. Переданные параметры просто передаются в
     * {@link Application#launch(String...)}. После завершения запуска будет
     * вызван {@link #start(Stage)}, где и происходит проверка аутентификации.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
