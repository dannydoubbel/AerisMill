module be.doebi.aerismill {
    requires javafx.controls;
    requires javafx.fxml;


    opens be.doebi.aerismill to javafx.fxml;
    exports be.doebi.aerismill;
}