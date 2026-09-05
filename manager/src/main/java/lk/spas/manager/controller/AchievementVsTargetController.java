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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import lk.spas.manager.model.AchievementVsTargetDto;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.service.ExecutiveService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.CurrencyFormatter;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.TaskExecutor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AchievementVsTargetController {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");
    @FXML private VBox root;
    @FXML private ComboBox<FilterOption> executiveComboBox;
    @FXML private TextField startMonthField;
    @FXML private TextField endMonthField;
    @FXML private Label startMonthValidationLabel;
    @FXML private Label endMonthValidationLabel;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label emptyLabel;
    @FXML private HBox errorBanner;
    @FXML private Label errorLabel;
    @FXML private Button retryButton;
    @FXML private LineChart<String, Number> achievementChart;
    @FXML private TableView<AchievementVsTargetDto> achievementTable;
    @FXML private TableColumn<AchievementVsTargetDto, String> seNameColumn;
    @FXML private TableColumn<AchievementVsTargetDto, String> levelColumn;
    @FXML private TableColumn<AchievementVsTargetDto, YearMonth> monthColumn;
    @FXML private TableColumn<AchievementVsTargetDto, BigDecimal> targetAmountColumn;
    @FXML private TableColumn<AchievementVsTargetDto, BigDecimal> achievedAmountColumn;
    @FXML private TableColumn<AchievementVsTargetDto, Double> achievementPercentageColumn;

    private final DashboardService dashboardService = new DashboardService();
    private final ExecutiveService executiveService = new ExecutiveService();
    private DashboardStateView stateView;

    @FXML
    private void initialize() {
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
                errorLabel, retryButton, achievementTable, refreshButton, null);
        seNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeName()));
        levelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeLevelName()));
        monthColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMonth()));
        targetAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTargetAmount()));
        achievedAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAchievedAmount()));
        achievementPercentageColumn.setCellValueFactory(
                data -> new SimpleObjectProperty<>(data.getValue().getAchievementPercentage()));
        monthColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(YearMonth value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.toString());
            }
        });
        targetAmountColumn.setCellFactory(column -> currencyCell());
        achievedAmountColumn.setCellFactory(column -> currencyCell());
        achievementPercentageColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.1f%%", value));
            }
        });
        loadExecutiveOptions();
    }

    private TableCell<AchievementVsTargetDto, BigDecimal> currencyCell() {
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
        loadAchievementData();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        ManagerShellController shell = ManagerShellController.fromNode((Node) event.getSource());
        if (shell != null) {
            shell.showDashboard();
        }
    }

    private void loadExecutiveOptions() {
        stateView.showLoading();
        Task<List<SalesExecutive>> task = new Task<>() {
            @Override
            protected List<SalesExecutive> call() throws Exception {
                return executiveService.getExecutives(0, 100);
            }
        };
        task.setOnSucceeded(event -> {
            List<FilterOption> options = new ArrayList<>();
            options.add(new FilterOption(null, "All"));
            for (SalesExecutive executive : task.getValue()) {
                options.add(new FilterOption(executive.getId(), executive.getFullName()));
            }
            executiveComboBox.setItems(FXCollections.observableArrayList(options));
            executiveComboBox.getSelectionModel().selectFirst();
            loadAchievementData();
        });
        task.setOnFailed(event -> showFailure(task.getException()));
        TaskExecutor.submit(task);
    }

    private void loadAchievementData() {
        String startMonth = startMonthField.getText().trim();
        String endMonth = endMonthField.getText().trim();
        clearValidation(startMonthValidationLabel, startMonth);
        clearValidation(endMonthValidationLabel, endMonth);
        if (!validateMonth(startMonth, startMonthValidationLabel)
                || !validateMonth(endMonth, endMonthValidationLabel)) {
            return;
        }

        Integer seId = selectedId();
        stateView.showLoading();
        achievementChart.setVisible(false);
        achievementChart.setManaged(false);
        Task<List<AchievementVsTargetDto>> task = new Task<>() {
            @Override
            protected List<AchievementVsTargetDto> call() throws Exception {
                return dashboardService.getAchievementVsTarget(
                        seId, emptyToNull(startMonth), emptyToNull(endMonth));
            }
        };
        task.setOnSucceeded(event -> {
            List<AchievementVsTargetDto> data = task.getValue();
            achievementTable.setItems(FXCollections.observableArrayList(data));
            if (data.isEmpty()) {
                stateView.showEmpty("No achievement data found for the selected filters.");
            } else {
                populateChart(data);
                achievementChart.setVisible(true);
                achievementChart.setManaged(true);
                stateView.showContent();
            }
        });
        task.setOnFailed(event -> showFailure(task.getException()));
        TaskExecutor.submit(task);
    }

    private Integer selectedId() {
        FilterOption selected = executiveComboBox.getValue();
        return selected == null ? null : selected.id;
    }

    private void populateChart(List<AchievementVsTargetDto> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (AchievementVsTargetDto achievement : data) {
            series.getData().add(new XYChart.Data<>(achievement.getMonth().toString(),
                    achievement.getAchievementPercentage()));
        }
        achievementChart.getData().setAll(List.of(series));
    }

    private void showFailure(Throwable failure) {
        if (failure instanceof ApiException) {
            ApiException apiFailure = (ApiException) failure;
            if (apiFailure.isBadRequest()) {
                stateView.showError(apiFailure.getMessage(), this::loadAchievementData);
                return;
            }
            if (apiFailure.isAuthError()) {
                AuthRedirector.redirectToLogin(root);
                return;
            }
        }
        stateView.showError(failure == null || failure.getMessage() == null
                ? "Unable to load achievement data." : failure.getMessage(), this::loadAchievementData);
    }

    private boolean validateMonth(String value, Label validationLabel) {
        if (value.isEmpty()) {
            return true;
        }
        try {
            if (value.matches("\\d{4}-\\d{2}")) {
                YearMonth.parse(value, MONTH_FORMAT);
                return true;
            }
        } catch (DateTimeParseException ignored) {
            // Inline validation message below explains the accepted format.
        }
        validationLabel.setText("Use yyyy-MM.");
        validationLabel.setVisible(true);
        validationLabel.setManaged(true);
        return false;
    }

    private void clearValidation(Label validationLabel, String value) {
        if (value.isEmpty()) {
            validationLabel.setText("");
            validationLabel.setVisible(false);
            validationLabel.setManaged(false);
        }
    }

    private String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private static final class FilterOption {
        private final Integer id;
        private final String label;

        private FilterOption(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}