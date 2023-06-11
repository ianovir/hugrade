module com.ianovir.hugrade.data.main {
    requires com.ianovir.hugrade.core.main;
    requires com.google.gson;

    opens com.ianovir.hugrade.data.dtos to com.google.gson;

    exports com.ianovir.hugrade.data.dtos;
    exports com.ianovir.hugrade.data.plugout;
    exports com.ianovir.hugrade.data;
}