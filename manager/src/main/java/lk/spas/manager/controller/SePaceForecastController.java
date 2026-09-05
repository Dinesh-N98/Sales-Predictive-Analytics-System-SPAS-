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
import lk.spas.manager.model.SePaceForecastDto;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.CurrencyFormatter;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.TaskExecutor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SePaceForecastController {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");
    @FXML private VBox root;
    @FXML private Button refreshButton;
    @FXML private TextField monthField;
    @FXML private Label monthValidationLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label emptyLabel;
    @FXML private HBox errorBanner;
    @FXML private Label errorLabel;
    @FXML private Button retryButton;
    @FXML private TableView<SePaceForecastDto> forecastTable;
    @FXML private TableColumn<SePaceForecastDto, String> seNameColumn;
    @FXML private TableColumn<SePaceForecastDto, String> levelColumn;
    @FXML private TableColumn<SePaceForecastDto, BigDecimal> targetAmountColumn;
    @FXML private TableColumn<SePaceForecastDto, BigDecimal> achievedAmountColumn;
    @FXML private TableColumn<SePaceForecastDto, Integer> predictionColumn;
    @FXML private TableColumn<SePaceForecastDto, Double> probabilityColumn;

    private final DashboardService dashboardService = new DashboardService();
    private DashboardStateView stateView;

    @FXML
    private void initialize() {
        monthField.setText(YearMonth.now().toString());
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
                errorLabel, retryButton, forecastTable, refreshButton, monthField);
        seNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeName()));
        levelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeLevelName()));
        targetAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTargetAmount()));
        achievedAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAchievedAmount()));
        predictionColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrediction()));
        probabilityColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProbabilityHitTarget()));
        targetAmountColumn.setCellFactory(column -> currencyCell());
        achievedAmountColumn.setCellFactory(column -> currencyCell());
        predictionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value == 1 ? "Yes" : "No");
            }
        });
        probabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.1f%%", value * 100));
            }
        });
        loadForecast();
    }

    private TableCell<SePaceForecastDto, BigDecimal> currencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : CurrencyFormatter.format(value));
            }
        };
    }

    @FXML
    private void handleRefresh() {
        loadForecast();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        ManagerShellController shell = ManagerShellController.fromNode((Node) event.getSource());
        if (shell != null) {
            shell.showDashboard();
        }
    }

    private void loadForecast() {
        String month = monthField.getText().trim();
        clearValidationMessage();
        if (!isValidMonth(month)) {
            monthValidationLabel.setText("Enter a valid month as yyyy-MM.");
            monthValidationLabel.setVisible(true);
            monthValidationLabel.setManaged(true);
            return;
        }
        stateView.showLoading();

        Task<List<SePaceForecastDto>> loadTask = new Task<>() {
            @Override
            protected List<SePaceForecastDto> call() throws Exception {
                return dashboardService.getSePaceForecast(month);
            }
        };
        loadTask.setOnSucceeded(event -> {
            List<SePaceForecastDto> data = loadTask.getValue();
            forecastTable.setItems(FXCollections.observableArrayList(data));
            if (data.isEmpty()) {
                stateView.showEmpty("No active sales executives found for this month.");
            } else {
                stateView.showContent();
            }
        });
        loadTask.setOnFailed(event -> {
            Throwable failure = loadTask.getException();
            if (failure instanceof ApiException) {
                ApiException apiFailure = (ApiException) failure;
                if (apiFailure.isBadRequest()) {
                    stateView.showError(apiFailure.getMessage(), this::loadForecast);
                    return;
                }
                if (apiFailure.isServiceUnavailable()) {
                    stateView.showError(
                            "SE pace forecast is temporarily unavailable. Please try again.",
                            this::loadForecast);
                    return;
                }
                if (apiFailure.isAuthError()) {
                    AuthRedirector.redirectToLogin(root);
                    return;
                }
            }
            stateView.showError(failure == null || failure.getMessage() == null
                    ? "Unable to load SE pace forecast." : failure.getMessage(), this::loadForecast);
        });
        TaskExecutor.submit(loadTask);
    }

    private boolean isValidMonth(String month) {
        try {
            return month.matches("\\d{4}-\\d{2}") && YearMonth.parse(month, MONTH_FORMAT) != null;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private void clearValidationMessage() {
        monthValidationLabel.setText("");
        monthValidationLabel.setVisible(false);
        monthValidationLabel.setManaged(false);
    }
}