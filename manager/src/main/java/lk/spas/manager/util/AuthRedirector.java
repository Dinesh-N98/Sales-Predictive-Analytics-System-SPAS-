package lk.spas.manager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class AuthRedirector {
    private AuthRedirector() {
    }

    public static void redirectToLogin(Node source) {
        try {
            Parent login = FXMLLoader.load(source.getClass().getResource("/lk/spas/manager/login.fxml"));
            Stage stage = (Stage) source.getScene().getWindow();
            Scene scene = new Scene(login, 900, 600);
            scene.getStylesheets().add(source.getClass().getResource(
                    "/lk/spas/manager/css/theme-style.css").toExternalForm());
            stage.setTitle("SPAS - Manager Portal");
            stage.setScene(scene);
        } catch (IOException ignored) {
            // The screen cannot be redirected until JavaFX has attached its scene.
        }
    }
}