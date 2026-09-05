package lk.spas.manager.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.spas.manager.exception.ApiException;
import lk.spas.manager.model.SePerformanceDto;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.CurrencyFormatter;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.TaskExecutor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SePerformanceController {
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
    @FXML private TableView<SePerformanceDto> performanceTable;
    @FXML private TableColumn<SePerformanceDto, String> seNameColumn;
    @FXML private TableColumn<SePerformanceDto, String> levelColumn;
    @FXML private TableColumn<SePerformanceDto, Integer> salesCountColumn;
    @FXML private TableColumn<SePerformanceDto, BigDecimal> totalSalesAmountColumn;
    @FXML private TableColumn<SePerformanceDto, BigDecimal> targetAmountColumn;
    @FXML private TableColumn<SePerformanceDto, BigDecimal> achievedAmountColumn;
    @FXML private TableColumn<SePerformanceDto, Double> achievementPercentageColumn;

    private final DashboardService dashboardService = new DashboardService();
    private DashboardStateView stateView;

    @FXML
    private void initialize() {
        monthField.setText(YearMonth.now().toString());
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
                errorLabel, retryButton, performanceTable, refreshButton, monthField);
        seNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeName()));
        levelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeLevelName()));
        salesCountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSalesCount()));
        totalSalesAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalSalesAmount()));
        targetAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTargetAmount()));
        achievedAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAchievedAmount()));
        achievementPercentageColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAchievementPercentage()));
        totalSalesAmountColumn.setCellFactory(column -> currencyCell());
        targetAmountColumn.setCellFactory(column -> currencyCell());
        achievedAmountColumn.setCellFactory(column -> currencyCell());
        achievementPercentageColumn.setCellFactory(column -> new TableCell<SePerformanceDto, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.1f%%", value));
            }
        });
        loadPerformance();
    }

    private TableCell<SePerformanceDto, BigDecimal> currencyCell() {
        return new TableCell<SePerformanceDto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : CurrencyFormatter.format(value));
            }
        };
    }

    @FXML
    private void handleRefresh() {
        loadPerformance();
    }

    private void loadPerformance() {
        String month = monthField.getText().trim();
        clearValidationMessage();
        if (!isValidMonth(month)) {
            monthValidationLabel.setText("Enter a valid month as yyyy-MM.");
            monthValidationLabel.setVisible(true);
            monthValidationLabel.setManaged(true);
            return;
        }
        stateView.showLoading();

        Task<List<SePerformanceDto>> loadTask = new Task<List<SePerformanceDto>>() {
            @Override
            protected List<SePerformanceDto> call() throws Exception {
                return dashboardService.getSePerformance(month);
            }
        };
        loadTask.setOnSucceeded(event -> {
            List<SePerformanceDto> data = loadTask.getValue();
            performanceTable.setItems(FXCollections.observableArrayList(data));
            if (data.isEmpty()) {
                stateView.showEmpty("No performance data found for this month.");
            } else {
                stateView.showContent();
            }
        });
        loadTask.setOnFailed(event -> {
            Throwable failure = loadTask.getException();
            if (failure instanceof ApiException) {
                ApiException apiFailure = (ApiException) failure;
                if (apiFailure.isBadRequest()) {
                    stateView.showError(apiFailure.getMessage(), this::loadPerformance);
                    return;
                }
                if (apiFailure.isAuthError()) {
                    AuthRedirector.redirectToLogin(root);
                    return;
                }
            }
            stateView.showError(failure == null || failure.getMessage() == null
                    ? "Unable to load SE performance." : failure.getMessage(), this::loadPerformance);
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