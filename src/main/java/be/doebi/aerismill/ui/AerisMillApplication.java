package be.doebi.aerismill.ui;

import be.doebi.aerismill.service.UIStateService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AerisMillApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AerisMillApplication.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                Objects.requireNonNull(AerisMillApplication.class.getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
        stage.setTitle("AerisMill");
        stage.setScene(scene);
        UIStateService.getInstance().restoreWindowState(stage);
        stage.show();

    }
}
