package be.doebi.aerismill.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AerisMillApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AerisMillApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(
                AerisMillApplication.class.getResource("/be/doebi/aerismill/ui/app.css").toExternalForm()
        );
        stage.setTitle("AerisMill");
        stage.setScene(scene);
        stage.show();
    }
}
