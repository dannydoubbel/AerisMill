module be.doebi.aerismill {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fazecast.jSerialComm;

    exports be.doebi.aerismill.ui;
    opens be.doebi.aerismill.ui to javafx.fxml;
}