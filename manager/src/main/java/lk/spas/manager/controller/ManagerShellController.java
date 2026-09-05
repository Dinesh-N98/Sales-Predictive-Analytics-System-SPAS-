package lk.spas.manager.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.spas.manager.util.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ManagerShellController {

    @FXML private BorderPane root;
    @FXML private VBox sidebar;
    @FXML private StackPane contentArea;
    @FXML private Button dashboardButton;
    @FXML private Button decisionAidButton;
    @FXML private Button manageExecutivesButton;
    @FXML private Button logoutButton;

    private Button activeNavButton;
    private TabPane dashboardTabPane;
    private final Map<Tab, String> dashboardTabResources = new HashMap<>();
    private final Set<String> loadedDashboardResources = new HashSet<>();

    @FXML
    public void initialize() {
        root.getProperties().put("managerShellController", this);
        if (!SessionManager.getInstance().hasSession()) {
            Platform.runLater(this::redirectToLogin);
            return;
        }

        showDashboard();
    }

    public static ManagerShellController fromNode(Node source) {
        if (source == null || source.getScene() == null || source.getScene().getRoot() == null) {
            return null;
        }
        if (!(source.getScene().getRoot() instanceof BorderPane)) {
            return null;
        }
        return (ManagerShellController) ((BorderPane) source.getScene().getRoot()).getProperties().get("managerShellController");
    }

    @FXML
    public void showDashboard() {
        setActiveButton(dashboardButton);
        if (dashboardTabPane == null) {
            dashboardTabPane = buildDashboardTabPane();
        }
        dashboardTabPane.getSelectionModel().selectFirst();
        contentArea.getChildren().setAll(dashboardTabPane);
    }

    @FXML
    private void showDecisionAid() {
        setActiveButton(decisionAidButton);
        try {
            Parent panel = FXMLLoader.load(getClass().getResource("/lk/spas/manager/decision-aid.fxml"));
            contentArea.getChildren().setAll(panel);
        } catch (IOException ex) {
            Label message = new Label("Unable to load Decision Aid.");
            message.getStyleClass().addAll("heading-2", "placeholder-label");
            contentArea.getChildren().setAll(message);
        }
    }

    @FXML
    private void showManageExecutives() {
        setActiveButton(manageExecutivesButton);
        try {
            Parent panel = FXMLLoader.load(getClass().getResource("/lk/spas/manager/manage-executive.fxml"));
            contentArea.getChildren().setAll(panel);
        } catch (IOException ex) {
            Label message = new Label("Unable to load Manage Executives.");
            message.getStyleClass().addAll("heading-2", "placeholder-label");
            contentArea.getChildren().setAll(message);
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        logoutAndRedirect(logoutButton);
    }

    public static void logoutAndRedirect(Node source) throws IOException {
        SessionManager.getInstance().clear();
        Parent root = FXMLLoader.load(ManagerShellController.class.getResource("/lk/spas/manager/login.fxml"));
        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(ManagerShellController.class.getResource("/lk/spas/manager/css/theme-style.css").toExternalForm());
        stage.setTitle("SPAS - Manager Portal");
        stage.setScene(scene);
    }

    private TabPane buildDashboardTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        addDashboardTab(tabPane, "Hot Leads", "/lk/spas/manager/hot-leads.fxml");
        addDashboardTab(tabPane, "At-Risk Activities", "/lk/spas/manager/at-risk-activities.fxml");
        addDashboardTab(tabPane, "SE Pace Forecast", "/lk/spas/manager/se-pace-forecast.fxml");
        addDashboardTab(tabPane, "SE Performance", "/lk/spas/manager/se-performance.fxml");
        addDashboardTab(tabPane, "Sales Trends", "/lk/spas/manager/sales-trends.fxml");
        addDashboardTab(tabPane, "Achievement vs Target", "/lk/spas/manager/achievement-vs-target.fxml");

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) {
                return;
            }
            String resourcePath = dashboardTabResources.get(newTab);
            if (resourcePath != null && !loadedDashboardResources.contains(resourcePath)) {
                loadDashboardTabContent(newTab, resourcePath);
            }
        });
        return tabPane;
    }

    private void addDashboardTab(TabPane tabPane, String title, String resourcePath) {
        Tab tab = new Tab(title);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        dashboardTabResources.put(tab, resourcePath);
    }

    private void loadDashboardTabContent(Tab tab, String resourcePath) {
        try {
            Parent panel = FXMLLoader.load(getClass().getResource(resourcePath));
            tab.setContent(panel);
            loadedDashboardResources.add(resourcePath);
        } catch (IOException ex) {
            Label message = new Label("Unable to load " + tab.getText() + ".");
            message.getStyleClass().addAll("heading-2", "placeholder-label");
            tab.setContent(message);
            loadedDashboardResources.add(resourcePath);
        }
    }

    private void setActiveButton(Button button) {
        if (activeNavButton != null && activeNavButton != button) {
            activeNavButton.getStyleClass().remove("active");
        }
        if (!button.getStyleClass().contains("active")) {
            button.getStyleClass().add("active");
        }
        activeNavButton = button;
    }

    private void redirectToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/spas/manager/login.fxml"));
            Stage stage = (Stage) this.root.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/lk/spas/manager/css/theme-style.css").toExternalForm());
            stage.setTitle("SPAS - Manager Portal");
            stage.setScene(scene);
        } catch (IOException ignored) {
            // The screen cannot be redirected until JavaFX has attached its scene.
        }
    }
}
