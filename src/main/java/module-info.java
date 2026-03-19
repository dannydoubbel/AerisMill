module be.doebi.aerismill {
    requires javafx.controls;
    requires javafx.fxml;

    exports be.doebi.aerismill.ui;
    opens be.doebi.aerismill.ui to javafx.fxml;
}