module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.ianovir.hugrade.controllers to javafx.fxml;
    exports com.ianovir.hugrade;

}