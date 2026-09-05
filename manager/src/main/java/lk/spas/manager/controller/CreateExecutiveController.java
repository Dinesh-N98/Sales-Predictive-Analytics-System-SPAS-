package lk.spas.manager.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lk.spas.manager.model.CreateExecutiveRequest;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.model.SeLevel;
import lk.spas.manager.service.ExecutiveService;
import lk.spas.manager.util.TaskExecutor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreateExecutiveController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<SeLevel> levelBox;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox activeBox;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private final ExecutiveService executiveService = new ExecutiveService();
    private Runnable onSaved = () -> { };
    private boolean levelsLoading;
    private boolean saving;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        levelBox.setConverter(new StringConverter<SeLevel>() {
            @Override
            public String toString(SeLevel level) {
                return level == null ? "" : level.getLevelName();
            }

            @Override
            public SeLevel fromString(String value) {
                return null;
            }
        });
        passwordField.setText("cgi123");
        activeBox.setSelected(true);
        loadLevels();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved == null ? () -> { } : onSaved;
    }

    @FXML
    private void handleCreateExecutive() {
        String validationError = validateForm();
        if (validationError != null) {
            statusLabel.setText(validationError);
            return;
        }

        SeLevel selectedLevel = levelBox.getValue();
        CreateExecutiveRequest request = new CreateExecutiveRequest(
                fullNameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                selectedLevel.getId(),
                passwordField.getText(),
                activeBox.isSelected());

        saving = true;
        updateSaveState();
        statusLabel.setText("Creating executive...");

        Task<SalesExecutive> createTask = new Task<SalesExecutive>() {
            @Override
            protected SalesExecutive call() throws Exception {
                return executiveService.createExecutive(request);
            }
        };
        createTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                saving = false;
                updateSaveState();
                onSaved.run();
                closeWindow();
            });
        });
        createTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                saving = false;
                updateSaveState();
                statusLabel.setText(failureMessage(createTask.getException(),
                        "Failed to create executive."));
            });
        });
        TaskExecutor.submit(createTask);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void loadLevels() {
        levelsLoading = true;
        updateSaveState();
        statusLabel.setText("Loading executive levels...");

        Task<List<SeLevel>> loadTask = new Task<List<SeLevel>>() {
            @Override
            protected List<SeLevel> call() throws Exception {
                return executiveService.getSeLevels();
            }
        };
        loadTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                levelBox.setItems(FXCollections.observableArrayList(loadTask.getValue()));
                levelsLoading = false;
                updateSaveState();
                statusLabel.setText("Select an executive level.");
            });
        });
        loadTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                levelsLoading = false;
                updateSaveState();
                statusLabel.setText(failureMessage(loadTask.getException(),
                        "Unable to load executive levels."));
            });
        });
        TaskExecutor.submit(loadTask);
    }

    private String validateForm() {
        if (fullNameField.getText() == null || fullNameField.getText().isBlank()) {
            return "Full name is required.";
        }
        if (fullNameField.getText().trim().length() > 100) {
            return "Full name must not exceed 100 characters.";
        }
        if (phoneField.getText() == null
                || !phoneField.getText().matches("[0-9+()\\-\\s]{7,20}")) {
            return "Phone must contain 7 to 20 valid characters.";
        }
        if (emailField.getText() == null
                || !emailField.getText().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "A valid email is required.";
        }
        if (emailField.getText().trim().length() > 100) {
            return "Email must not exceed 100 characters.";
        }
        if (levelBox.getValue() == null) {
            return "Executive level is required.";
        }
        if (passwordField.getText() == null || passwordField.getText().isBlank()) {
            return "Password is required.";
        }
        if (passwordField.getText().length() > 255) {
            return "Password must not exceed 255 characters.";
        }
        return null;
    }

    private void updateSaveState() {
        if (saveButton != null) {
            saveButton.setDisable(levelsLoading || saving);
        }
    }

    private String failureMessage(Throwable failure, String fallback) {
        return failure == null || failure.getMessage() == null || failure.getMessage().isBlank()
                ? fallback : failure.getMessage();
    }

    private void closeWindow() {
        if (statusLabel != null && statusLabel.getScene() != null
                && statusLabel.getScene().getWindow() != null) {
            statusLabel.getScene().getWindow().hide();
        }
    }
}
