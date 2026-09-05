package lk.spas.manager.util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public final class DashboardStateView {
    private final ProgressIndicator loadingIndicator;
    private final Label emptyLabel;
    private final HBox errorBanner;
    private final Label errorLabel;
    private final Button retryButton;
    private final TableView<?> content;
        private final Button refreshButton;
        private final TextField parameterField;

    public DashboardStateView(ProgressIndicator loadingIndicator, Label emptyLabel, HBox errorBanner,
            Label errorLabel, Button retryButton, TableView<?> content, Button refreshButton,
            TextField parameterField) {
        this.loadingIndicator = loadingIndicator;
        this.emptyLabel = emptyLabel;
        this.errorBanner = errorBanner;
        this.errorLabel = errorLabel;
        this.retryButton = retryButton;
        this.content = content;
        this.refreshButton = refreshButton;
        this.parameterField = parameterField;
    }

    public void showLoading() {
        setState(true, false, false);
    }

    public void showEmpty(String message) {
        emptyLabel.setText(message);
        setState(false, true, false);
    }

    public void showError(String message, Runnable retryAction) {
        errorLabel.setText(message);
        retryButton.setOnAction(event -> retryAction.run());
        setState(false, false, true);
    }

    public void showContent() {
        setState(false, false, false);
    }

    private void setState(boolean loading, boolean empty, boolean error) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
        errorBanner.setVisible(error);
        errorBanner.setManaged(error);
        content.setVisible(!loading && !empty && !error);
        content.setManaged(!loading && !empty && !error);
        if (refreshButton != null) {
            refreshButton.setDisable(loading);
        }
        if (parameterField != null) {
            parameterField.setDisable(loading);
        }
    }
}