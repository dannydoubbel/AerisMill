module be.doebi.aerismill {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fazecast.jSerialComm;

    opens be.doebi.aerismill.ui to javafx.fxml;
    exports be.doebi.aerismill.ui;
    exports be.doebi.aerismill.service;
}