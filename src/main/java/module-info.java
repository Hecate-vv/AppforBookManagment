module app.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.demo to javafx.fxml;
    exports app.demo;
}