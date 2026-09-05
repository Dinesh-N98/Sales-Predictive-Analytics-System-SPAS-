package lk.spas.manager.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.spas.manager.exception.ApiException;
import lk.spas.manager.model.AtRiskActivityDto;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.TaskExecutor;

import java.io.IOException;
import java.util.List;

public class AtRiskActivitiesController {
    @FXML private VBox root;
    @FXML private Button refreshButton;
    @FXML private TextField limitField;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label emptyLabel;
    @FXML private HBox errorBanner;
    @FXML private Label errorLabel;
    @FXML private Button retryButton;
    @FXML private TableView<AtRiskActivityDto> atRiskActivitiesTable;
    @FXML private TableColumn<AtRiskActivityDto, String> seNameColumn;
    @FXML private TableColumn<AtRiskActivityDto, String> activityNameColumn;
    @FXML private TableColumn<AtRiskActivityDto, String> predictionColumn;
    @FXML private TableColumn<AtRiskActivityDto, Double> probabilityRejectedColumn;

    private final DashboardService dashboardService = new DashboardService();
    private DashboardStateView stateView;

    @FXML
    private void initialize() {
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
            errorLabel, retryButton, atRiskActivitiesTable, refreshButton, limitField);
        seNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeName()));
        activityNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivityName()));
        predictionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrediction()));
        probabilityRejectedColumn.setCellValueFactory(
                data -> new SimpleObjectProperty<>(data.getValue().getProbabilityRejected()));
        probabilityRejectedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.1f%%", value * 100));
            }
        });
        loadAtRiskActivities();
    }

    @FXML
    private void handleRefresh() {
        loadAtRiskActivities();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        ManagerShellController shell = ManagerShellController.fromNode((Node) event.getSource());
        if (shell != null) {
            shell.showDashboard();
        }
    }

    private void loadAtRiskActivities() {
        Integer limit = parseLimit();
        if (limit == null) {
            stateView.showError("Enter a numeric limit.", this::loadAtRiskActivities);
            return;
        }
        stateView.showLoading();

        Task<List<AtRiskActivityDto>> loadTask = new Task<>() {
            @Override
            protected List<AtRiskActivityDto> call() throws Exception {
                return dashboardService.getAtRiskActivities(limit);
            }
        };
        loadTask.setOnSucceeded(event -> {
            List<AtRiskActivityDto> data = loadTask.getValue();
            atRiskActivitiesTable.setItems(FXCollections.observableArrayList(data));
            if (data.isEmpty()) {
                stateView.showEmpty("No at-risk activities found.");
            } else {
                stateView.showContent();
            }
        });
        loadTask.setOnFailed(event -> {
            Throwable failure = loadTask.getException();
            if (failure instanceof ApiException) {
                ApiException apiFailure = (ApiException) failure;
                if (apiFailure.isAuthError()) {
                    AuthRedirector.redirectToLogin(root);
                    return;
                }
                if (apiFailure.isServiceUnavailable()) {
                    stateView.showError(
                            "At-risk activities service is temporarily unavailable. Please try again.",
                            this::loadAtRiskActivities);
                    return;
                }
            }
            stateView.showError(failure == null || failure.getMessage() == null
                    ? "Unable to load at-risk activities." : failure.getMessage(),
                    this::loadAtRiskActivities);
        });
        TaskExecutor.submit(loadTask);
    }

    private Integer parseLimit() {
        try {
            return Integer.valueOf(limitField.getText().trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}