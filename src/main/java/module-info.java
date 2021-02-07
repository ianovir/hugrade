module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.ianovir.hugrade.controllers to javafx.fxml;
    opens com.ianovir.hugrade.views to javafx.fxml;
    opens com.ianovir.hugrade.core.models to com.google.gson;

    exports com.ianovir.hugrade;

}