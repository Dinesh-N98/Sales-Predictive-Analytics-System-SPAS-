package lk.spas.manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import java.io.IOException;
import lk.spas.manager.service.AuthService;
import lk.spas.manager.model.LoginResponse;
import lk.spas.manager.util.SessionManager;
import lk.spas.manager.util.TaskExecutor;

public class LoginController {
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingSpinner;
    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String phone = phoneField.getText();
        String password = passwordField.getText();
        errorLabel.setVisible(false);
        loadingSpinner.setVisible(true);
        loadingSpinner.setManaged(true);

        Task<LoginResponse> loginTask = new Task<>() {
            @Override
            protected LoginResponse call() throws Exception {
                return authService.login(phone, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                LoginResponse resp = loginTask.getValue();
                SessionManager.getInstance().setToken(resp.getToken());
                SessionManager.getInstance().setManagerName(resp.getFullName());
                navigateToHome();
            });
        });

        loginTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                SessionManager.getInstance().clear();
                Throwable failure = loginTask.getException();
                errorLabel.setText(failureMessage(failure, "Unable to sign in."));
                errorLabel.setVisible(true);
            });
        });

        TaskExecutor.submit(loginTask);
    }

    private void navigateToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/spas/manager/manager-shell.fxml"));
            Stage stage = (Stage) phoneField.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 760);
            scene.getStylesheets().add(getClass().getResource("/lk/spas/manager/css/theme-style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SPAS - Manager Portal");
        } catch (IOException ex) {
            errorLabel.setText("Failed to load dashboard.");
            errorLabel.setVisible(true);
        }
    }

    private String failureMessage(Throwable failure, String fallback) {
        return failure == null || failure.getMessage() == null || failure.getMessage().isBlank()
                ? fallback : failure.getMessage();
    }
}