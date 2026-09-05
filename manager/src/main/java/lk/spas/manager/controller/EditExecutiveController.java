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

public class EditExecutiveController implements Initializable {

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
    private SalesExecutive executive;
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
        passwordField.setPromptText("Leave blank to keep current password");
        loadLevels();
    }

    public void setExecutive(SalesExecutive executive) {
        this.executive = executive;
        if (executive == null) {
            statusLabel.setText("No executive selected.");
            updateSaveState();
            return;
        }
        fullNameField.setText(executive.getFullName());
        phoneField.setText(executive.getPhoneNumber());
        emailField.setText(executive.getEmail());
        activeBox.setSelected(executive.isActive());
        selectMatchingLevel();
        updateSaveState();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved == null ? () -> { } : onSaved;
    }

    @FXML
    private void handleUpdateExecutive() {
        if (executive == null) {
            statusLabel.setText("No executive selected.");
            return;
        }

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
        statusLabel.setText("Updating executive...");

        Task<SalesExecutive> updateTask = new Task<SalesExecutive>() {
            @Override
            protected SalesExecutive call() throws Exception {
                return executiveService.updateExecutive(executive.getId(), request);
            }
        };
        updateTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                saving = false;
                updateSaveState();
                onSaved.run();
                closeWindow();
            });
        });
        updateTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                saving = false;
                updateSaveState();
                statusLabel.setText(failureMessage(updateTask.getException(),
                        "Failed to update executive."));
            });
        });
        TaskExecutor.submit(updateTask);
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
                selectMatchingLevel();
                updateSaveState();
                if (executive != null) {
                    statusLabel.setText("Review the executive details.");
                }
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

    private void selectMatchingLevel() {
        if (executive == null || levelBox.getItems() == null) {
            return;
        }
        levelBox.getItems().stream()
                .filter(level -> level.getId() == executive.getSeLevelId())
                .findFirst()
                .ifPresent(level -> levelBox.getSelectionModel().select(level));
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
        if (passwordField.getText() != null && passwordField.getText().length() > 255) {
            return "Password must not exceed 255 characters.";
        }
        return null;
    }

    private void updateSaveState() {
        if (saveButton != null) {
            saveButton.setDisable(levelsLoading || saving || executive == null);
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
