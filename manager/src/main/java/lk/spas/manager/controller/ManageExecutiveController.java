package lk.spas.manager.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.application.Platform;
import lk.spas.manager.model.CreateExecutiveRequest;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.service.ExecutiveService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.SessionManager;
import lk.spas.manager.util.TaskExecutor;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageExecutiveController implements Initializable {

    @FXML private VBox root;

    @FXML private TableView<SalesExecutive> executiveTable;
    @FXML private TableColumn<SalesExecutive, String> colName;
    @FXML private TableColumn<SalesExecutive, String> colPhone;
    @FXML private TableColumn<SalesExecutive, String> colEmail;
    @FXML private TableColumn<SalesExecutive, String> colLevel;
    @FXML private TableColumn<SalesExecutive, Boolean> colStatus;
    @FXML private Label statusLabel;
    @FXML private Button newExecutiveButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;
    @FXML private javafx.scene.control.ProgressIndicator loadingIndicator;
    @FXML private Label emptyLabel;
    @FXML private javafx.scene.layout.HBox errorBanner;
    @FXML private Label errorLabel;
    @FXML private Button retryButton;

    private final ExecutiveService executiveService = new ExecutiveService();
    private DashboardStateView stateView;
    private static final int PAGE_SIZE = 20;
    private int currentPage;
    private boolean loading;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionManager.getInstance().hasSession()) {
            Platform.runLater(() -> AuthRedirector.redirectToLogin(root));
            return;
        }
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
            errorLabel, retryButton, executiveTable, refreshButton, null);
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("seLevelName"));
        colName.setCellFactory(column -> createTooltipCell());
        colEmail.setCellFactory(column -> createTooltipCell());
        colStatus.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isActive()).asObject());
        colStatus.setCellFactory(column -> new TableCell<SalesExecutive, Boolean>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                setText(null);

                if (empty || active == null) {
                    badge.setOnMouseClicked(null);
                    setGraphic(null);
                    return;
                }

                badge.setText(active ? "Active" : "Inactive");
                badge.getStyleClass().removeAll("badge-status", "status-active", "status-inactive");
                badge.getStyleClass().addAll("badge-status", active ? "status-active" : "status-inactive");
                badge.setCursor(Cursor.HAND);
                badge.setDisable(false);
                badge.setOnMouseClicked(event -> {
                    SalesExecutive executive = getTableView().getItems().get(getIndex());
                    handleToggleStatus(executive, badge);
                    event.consume();
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(badge);
            }
        });
        executiveTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !executiveTable.getSelectionModel().isEmpty()) {
                openEditExecutive();
            }
        });

        loadExecutives();
    }

    private TableCell<SalesExecutive, String> createTooltipCell() {
        return new TableCell<SalesExecutive, String>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                setGraphic(null);
                setTooltip(null);
                setText(empty || value == null ? null : value);
                if (!empty && value != null && !value.isBlank()) {
                    setTooltip(new Tooltip(value));
                }
            }
        };
    }

    @FXML
    private void handleNewExecutive() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/lk/spas/manager/create-executive.fxml"));
            Parent popupRoot = loader.load();
            CreateExecutiveController controller = loader.getController();
            controller.setOnSaved(this::loadExecutives);
            showPopup(popupRoot, "New Executive");
        } catch (IOException exception) {
            statusLabel.setText("Unable to open the new executive form.");
        }
    }

    @FXML
    private void handleEditExecutive() {
        openEditExecutive();
    }

    private void openEditExecutive() {
        SalesExecutive executive = executiveTable.getSelectionModel().getSelectedItem();
        if (executive == null) {
            statusLabel.setText("Select an executive first.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/lk/spas/manager/edit-executive.fxml"));
            Parent popupRoot = loader.load();
            EditExecutiveController controller = loader.getController();
            controller.setExecutive(executive);
            controller.setOnSaved(this::loadExecutives);
            showPopup(popupRoot, "Edit Executive");
        } catch (IOException exception) {
            statusLabel.setText("Unable to open the executive editor.");
        }
    }

    private void showPopup(Parent popupRoot, String title) {
        Stage owner = (Stage) root.getScene().getWindow();
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(popupRoot);
        scene.getStylesheets().add(getClass().getResource(
                "/lk/spas/manager/css/theme-style.css").toExternalForm());
        popup.setTitle(title);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void handleToggleStatus(SalesExecutive executive, Label badge) {
        if (executive == null) {
            statusLabel.setText("Select an executive first.");
            return;
        }

        String action = executive.isActive() ? "deactivate" : "activate";
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to " + action + " " + executive.getFullName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm status change");
        confirmation.setHeaderText(null);
        if (confirmation.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        CreateExecutiveRequest request = new CreateExecutiveRequest(
                executive.getFullName(), executive.getPhoneNumber(), executive.getEmail(),
                executive.getSeLevelId(), "", !executive.isActive());
        badge.setDisable(true);
        statusLabel.setText("Updating executive status...");

        Task<SalesExecutive> statusTask = new Task<>() {
            @Override
            protected SalesExecutive call() throws Exception {
                return executiveService.updateExecutive(executive.getId(), request);
            }
        };
        statusTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                statusLabel.setText("Executive " + action + "d successfully.");
                loadExecutives();
            });
        });
        statusTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                badge.setDisable(false);
                Throwable failure = statusTask.getException();
                statusLabel.setText(failureMessage(failure, "Failed to update executive status."));
            });
        });
        TaskExecutor.submit(statusTask);
    }

    @FXML
    private void handleDeleteExecutive() {
        SalesExecutive executive = executiveTable.getSelectionModel().getSelectedItem();
        if (executive == null) {
            statusLabel.setText("Select an executive first.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + executive.getFullName() + "? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm executive deletion");
        confirmation.setHeaderText(null);
        if (confirmation.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        deleteButton.setDisable(true);
        statusLabel.setText("Deleting executive...");
        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                executiveService.deleteExecutive(executive.getId());
                return null;
            }
        };
        deleteTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                deleteButton.setDisable(false);
                statusLabel.setText("Executive deleted successfully.");
                loadExecutives();
            });
        });
        deleteTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                deleteButton.setDisable(false);
                Throwable failure = deleteTask.getException();
                statusLabel.setText(failure == null
                        ? "Executive cannot be deleted while business records reference it."
                        : failure.getMessage());
            });
        });
        TaskExecutor.submit(deleteTask);
    }

    @FXML
    private void handleRefresh() {
        loadExecutives();
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadExecutives();
        }
    }

    @FXML
    private void handleNextPage() {
        if (executiveTable.getItems().size() == PAGE_SIZE) {
            currentPage++;
            loadExecutives();
        }
    }

    private void loadExecutives() {
        loading = true;
        updateListControls();
        statusLabel.setText("Loading...");
        stateView.showLoading();

        Task<List<SalesExecutive>> loadTask = new Task<>() {
            @Override
            protected List<SalesExecutive> call() throws Exception {
                return executiveService.getExecutives(currentPage, PAGE_SIZE);
            }
        };

        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                ObservableList<SalesExecutive> data = FXCollections.observableArrayList(loadTask.getValue());
                executiveTable.setItems(data);
                loading = false;
                if (data.isEmpty()) {
                    stateView.showEmpty("No executives found.");
                } else {
                    stateView.showContent();
                }
                statusLabel.setText(data.isEmpty() ? "No executives found." : "Loaded " + data.size() + " executives.");
                updateListControls();
            });
        });

        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                loading = false;
                Throwable failure = loadTask.getException();
                if (failure instanceof lk.spas.manager.exception.ApiException
                        && ((lk.spas.manager.exception.ApiException) failure).isAuthError()) {
                    AuthRedirector.redirectToLogin(root);
                    return;
                }
                String message = failureMessage(failure, "Unable to load executives.");
                stateView.showError(message, this::loadExecutives);
                statusLabel.setText(message + " Use Refresh to try again.");
                updateListControls();
            });
        });

        TaskExecutor.submit(loadTask);
    }

    private void updateListControls() {
        if (refreshButton != null) refreshButton.setDisable(loading);
        if (previousPageButton != null) previousPageButton.setDisable(loading || currentPage == 0);
        if (nextPageButton != null) nextPageButton.setDisable(loading || executiveTable.getItems().size() < PAGE_SIZE);
        if (pageLabel != null) pageLabel.setText("Page " + (currentPage + 1));
    }

    private String failureMessage(Throwable failure, String fallback) {
        return failure == null || failure.getMessage() == null || failure.getMessage().isBlank()
                ? fallback : failure.getMessage();
    }
}