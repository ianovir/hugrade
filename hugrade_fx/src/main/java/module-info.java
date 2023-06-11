module hugrade.fx.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.ianovir.hugrade.core.main;
    requires com.ianovir.hugrade.data.main;

    opens com.ianovir.hugrade.fx.controllers to javafx.fxml;
    opens com.ianovir.hugrade.fx.views to javafx.fxml;

    exports com.ianovir.hugrade.fx;

}