package org.example.economicssimulatorclient;

import javafx.application.Platform;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.util.SessionManager;

public class MainApp extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
