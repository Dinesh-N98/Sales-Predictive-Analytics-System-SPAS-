package lk.spas.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.spas.manager.util.TaskExecutor;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/spas/manager/login.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/lk/spas/manager/css/theme-style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("SPAS - Manager Portal");
        stage.show();
    }

    @Override
    public void stop() {
        TaskExecutor.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }

}