package lk.spas.manager.controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lk.spas.manager.exception.ApiException;
import lk.spas.manager.model.ActivityOutcomeDetailDto;
import lk.spas.manager.model.AtRiskActivityDto;
import lk.spas.manager.model.HotLeadDto;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.model.SeTargetForecastDetailDto;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.service.ExecutiveService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.CurrencyFormatter;
import lk.spas.manager.util.TaskExecutor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecisionAidController {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");
    @FXML private VBox root;
    @FXML private ComboBox<ActivityOption> activityComboBox;
    @FXML private ComboBox<SalesExecutive> executiveComboBox;
    @FXML private TextField monthField;
    @FXML private Label monthValidationLabel;
    @FXML private Label activityErrorLabel;
    @FXML private Label forecastErrorLabel;
    @FXML private VBox activityResult;
    @FXML private VBox forecastResult;
    @FXML private Label activityPredictionLabel;
    @FXML private Label soldProbabilityLabel;
    @FXML private Label pendingProbabilityLabel;
    @FXML private Label rejectedProbabilityLabel;
    @FXML private ProgressBar soldBar;
    @FXML private ProgressBar pendingBar;
    @FXML private ProgressBar rejectedBar;
    @FXML private Label forecastLevelLabel;
    @FXML private Label targetAmountLabel;
    @FXML private Label achievedAmountLabel;
    @FXML private Label forecastPredictionLabel;
    @FXML private Label forecastProbabilityLabel;

    private final DashboardService dashboardService = new DashboardService();
    private final ExecutiveService executiveService = new ExecutiveService();

    @FXML
    private void initialize() {
        monthField.setText(YearMonth.now().toString());
        activityComboBox.setOnAction(event -> clear(activityErrorLabel));
        executiveComboBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(SalesExecutive value) { return value == null ? "" : value.getFullName(); }
            public SalesExecutive fromString(String value) { return null; }
        });
        loadOptions();
    }

    private void loadOptions() {
        Task<DecisionAidOptions> task = new Task<>() {
            @Override protected DecisionAidOptions call() throws Exception {
                List<HotLeadDto> hotLeads = dashboardService.getHotLeads(50);
                List<AtRiskActivityDto> atRisk = dashboardService.getAtRiskActivities(50);
                List<SalesExecutive> executives = executiveService.getExecutives(0, 100);
                Map<Integer, ActivityOption> unique = new LinkedHashMap<>();
                for (HotLeadDto item : hotLeads) {
                    unique.put(item.getActivityLogId(), new ActivityOption(item.getActivityLogId(), item.getSeName(), item.getActivityName()));
                }
                for (AtRiskActivityDto item : atRisk) {
                    unique.putIfAbsent(item.getActivityLogId(), new ActivityOption(item.getActivityLogId(), item.getSeName(), item.getActivityName()));
                }
                return new DecisionAidOptions(new ArrayList<>(unique.values()), executives);
            }
        };
        task.setOnSucceeded(event -> {
            DecisionAidOptions value = task.getValue();
            activityComboBox.setItems(FXCollections.observableArrayList(value.activities));
            executiveComboBox.setItems(FXCollections.observableArrayList(value.executives));
            activityComboBox.getSelectionModel().selectFirst();
            executiveComboBox.getSelectionModel().selectFirst();
        });
        task.setOnFailed(event -> showFailure(task.getException(), activityErrorLabel, "Unable to load Decision Aid options."));
        TaskExecutor.submit(task);
    }

    @FXML
    private void handleActivityPrediction() {
        ActivityOption selected = activityComboBox.getValue();
        if (selected == null) { activityErrorLabel.setText("Select an activity first."); return; }
        clear(activityErrorLabel);
        Task<ActivityOutcomeDetailDto> task = new Task<>() {
            @Override protected ActivityOutcomeDetailDto call() throws Exception {
                return dashboardService.getActivityOutcomeDetail(selected.id);
            }
        };
        task.setOnSucceeded(event -> showActivityResult(task.getValue()));
        task.setOnFailed(event -> showFailure(task.getException(), activityErrorLabel, "Unable to get activity prediction."));
        TaskExecutor.submit(task);
    }

    @FXML
    private void handleForecastPrediction() {
        SalesExecutive selected = executiveComboBox.getValue();
        String month = monthField.getText().trim();
        clear(forecastErrorLabel);
        monthValidationLabel.setText("");
        if (selected == null) { forecastErrorLabel.setText("Select a sales executive first."); return; }
        if (!validMonth(month)) { monthValidationLabel.setText("Enter a valid month as yyyy-MM."); return; }
        Task<SeTargetForecastDetailDto> task = new Task<>() {
            @Override protected SeTargetForecastDetailDto call() throws Exception {
                return dashboardService.getSeTargetForecastDetail(selected.getId(), month);
            }
        };
        task.setOnSucceeded(event -> showForecastResult(task.getValue()));
        task.setOnFailed(event -> showFailure(task.getException(), forecastErrorLabel, "Unable to get SE target forecast."));
        TaskExecutor.submit(task);
    }

    private void showActivityResult(ActivityOutcomeDetailDto value) {
        activityPredictionLabel.setText(value.getPrediction());
        setProbability(soldBar, soldProbabilityLabel, value.getProbabilitySold());
        setProbability(pendingBar, pendingProbabilityLabel, value.getProbabilityPending());
        setProbability(rejectedBar, rejectedProbabilityLabel, value.getProbabilityRejected());
        activityResult.setVisible(true); activityResult.setManaged(true);
    }

    private void showForecastResult(SeTargetForecastDetailDto value) {
        forecastLevelLabel.setText(value.getSeLevelName());
        targetAmountLabel.setText(CurrencyFormatter.format(value.getTargetAmount()));
        achievedAmountLabel.setText(CurrencyFormatter.format(value.getAchievedAmount()));
        forecastPredictionLabel.setText(value.getPrediction() == 1 ? "Yes" : "No");
        forecastProbabilityLabel.setText(percent(value.getProbabilityHitTarget()));
        forecastResult.setVisible(true); forecastResult.setManaged(true);
    }

    private void setProbability(ProgressBar bar, Label label, Double value) {
        double probability = value == null ? 0 : value;
        bar.setProgress(probability); label.setText(percent(probability));
    }

    private String percent(Double value) { return String.format("%.1f%%", (value == null ? 0 : value) * 100); }
    private boolean validMonth(String value) {
        try { return value.matches("\\d{4}-\\d{2}") && YearMonth.parse(value, MONTH_FORMAT) != null; }
        catch (DateTimeParseException ex) { return false; }
    }
    private void clear(Label label) { label.setText(""); }
    private void showFailure(Throwable failure, Label label, String fallback) {
        if (failure instanceof ApiException) {
            ApiException apiFailure = (ApiException) failure;
            if (apiFailure.isAuthError()) { AuthRedirector.redirectToLogin(root); return; }
            if (apiFailure.isNotFound()) { label.setText("No prediction available for this selection"); return; }
            if (apiFailure.isServiceUnavailable()) { label.setText("Prediction service is temporarily unavailable. Please try again."); return; }
        }
        label.setText(failure == null || failure.getMessage() == null ? fallback : failure.getMessage());
    }

    private static class ActivityOption {
        private final Integer id;
        private final String seName;
        private final String activityName;

        ActivityOption(Integer id, String seName, String activityName) {
            this.id = id;
            this.seName = seName;
            this.activityName = activityName;
        }

        @Override public String toString() { return seName + " - " + activityName; }
    }

    private static class DecisionAidOptions {
        private final List<ActivityOption> activities;
        private final List<SalesExecutive> executives;

        DecisionAidOptions(List<ActivityOption> activities, List<SalesExecutive> executives) {
            this.activities = activities;
            this.executives = executives;
        }
    }
}