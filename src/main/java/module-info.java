module com.example.coffee_shop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.coffee_shop to javafx.fxml;
    exports com.example.coffee_shop;
    exports com.example.coffee_shop.ProductModel;
    opens com.example.coffee_shop.ProductModel to javafx.fxml;
}