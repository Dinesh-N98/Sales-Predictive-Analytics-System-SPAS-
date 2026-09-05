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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.spas.manager.exception.ApiException;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.model.SalesTrendDto;
import lk.spas.manager.model.SeLevel;
import lk.spas.manager.service.DashboardService;
import lk.spas.manager.service.ExecutiveService;
import lk.spas.manager.util.AuthRedirector;
import lk.spas.manager.util.CurrencyFormatter;
import lk.spas.manager.util.DashboardStateView;
import lk.spas.manager.util.TaskExecutor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesTrendsController {
    @FXML private VBox root;
    @FXML private ComboBox<FilterOption> executiveComboBox;
    @FXML private ComboBox<FilterOption> levelComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label emptyLabel;
    @FXML private HBox errorBanner;
    @FXML private Label errorLabel;
    @FXML private Button retryButton;
    @FXML private LineChart<String, Number> trendChart;
    @FXML private TableView<SalesTrendDto> trendTable;
    @FXML private TableColumn<SalesTrendDto, LocalDate> dateColumn;
    @FXML private TableColumn<SalesTrendDto, Integer> salesCountColumn;
    @FXML private TableColumn<SalesTrendDto, BigDecimal> totalAmountColumn;

    private final DashboardService dashboardService = new DashboardService();
    private final ExecutiveService executiveService = new ExecutiveService();
    private DashboardStateView stateView;

    @FXML
    private void initialize() {
        stateView = new DashboardStateView(loadingIndicator, emptyLabel, errorBanner,
                errorLabel, retryButton, trendTable, refreshButton, null);
        dateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));
        salesCountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSalesCount()));
        totalAmountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalAmount()));
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.toString());
            }
        });
        totalAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : CurrencyFormatter.format(value));
            }
        });
        loadFilterOptions();
    }

    @FXML
    private void handleRefresh() {
        loadTrends();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        ManagerShellController shell = ManagerShellController.fromNode((Node) event.getSource());
        if (shell != null) {
            shell.showDashboard();
        }
    }

    private void loadFilterOptions() {
        stateView.showLoading();
        Task<FilterOptions> task = new Task<>() {
            @Override
            protected FilterOptions call() throws Exception {
                return new FilterOptions(executiveService.getExecutives(0, 100), executiveService.getSeLevels());
            }
        };
        task.setOnSucceeded(event -> {
            List<FilterOption> executives = new ArrayList<>();
            executives.add(new FilterOption(null, "All"));
            for (SalesExecutive executive : task.getValue().executives) {
                executives.add(new FilterOption(executive.getId(), executive.getFullName()));
            }
            List<FilterOption> levels = new ArrayList<>();
            levels.add(new FilterOption(null, "All"));
            for (SeLevel level : task.getValue().levels) {
                levels.add(new FilterOption(level.getId(), level.getLevelName()));
            }
            executiveComboBox.setItems(FXCollections.observableArrayList(executives));
            levelComboBox.setItems(FXCollections.observableArrayList(levels));
            executiveComboBox.getSelectionModel().selectFirst();
            levelComboBox.getSelectionModel().selectFirst();
            loadTrends();
        });
        task.setOnFailed(event -> showFailure(task.getException()));
        TaskExecutor.submit(task);
    }

    private void loadTrends() {
        Integer seId = selectedId(executiveComboBox);
        Integer seLevelId = selectedId(levelComboBox);
        String startDate = startDatePicker.getValue() == null ? null : startDatePicker.getValue().toString();
        String endDate = endDatePicker.getValue() == null ? null : endDatePicker.getValue().toString();
        stateView.showLoading();
        trendChart.setVisible(false);
        trendChart.setManaged(false);

        Task<List<SalesTrendDto>> task = new Task<>() {
            @Override
            protected List<SalesTrendDto> call() throws Exception {
                return dashboardService.getSalesTrends(seId, seLevelId, startDate, endDate);
            }
        };
        task.setOnSucceeded(event -> {
            List<SalesTrendDto> data = task.getValue();
            trendTable.setItems(FXCollections.observableArrayList(data));
            if (data.isEmpty()) {
                stateView.showEmpty("No sales found for the selected filters.");
            } else {
                populateChart(data);
                trendChart.setVisible(true);
                trendChart.setManaged(true);
                stateView.showContent();
            }
        });
        task.setOnFailed(event -> showFailure(task.getException()));
        TaskExecutor.submit(task);
    }

    private Integer selectedId(ComboBox<FilterOption> comboBox) {
        FilterOption selected = comboBox.getValue();
        return selected == null ? null : selected.id;
    }

    private void populateChart(List<SalesTrendDto> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (SalesTrendDto trend : data) {
            series.getData().add(new XYChart.Data<>(trend.getDate().toString(), trend.getTotalAmount()));
        }
        trendChart.getData().setAll(List.of(series));
    }

    private void showFailure(Throwable failure) {
        if (failure instanceof ApiException) {
            ApiException apiFailure = (ApiException) failure;
            if (apiFailure.isAuthError()) {
                AuthRedirector.redirectToLogin(root);
                return;
            }
            if (apiFailure.isBadRequest()) {
                stateView.showError(apiFailure.getMessage(), this::loadTrends);
                return;
            }
        }
        stateView.showError(failure == null || failure.getMessage() == null
                ? "Unable to load sales trends." : failure.getMessage(), this::loadTrends);
    }

    private static final class FilterOptions {
        private final List<SalesExecutive> executives;
        private final List<SeLevel> levels;

        private FilterOptions(List<SalesExecutive> executives, List<SeLevel> levels) {
            this.executives = executives;
            this.levels = levels;
        }
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