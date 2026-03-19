module be.doebi.aerismill {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    exports be.doebi.aerismill.ui;
    opens be.doebi.aerismill.ui to javafx.fxml;
}