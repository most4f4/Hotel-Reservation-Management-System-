module ca.proj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;


    opens ca.proj to javafx.fxml;
    exports ca.proj;
    exports ca.proj.Controllers;
    opens ca.proj.Controllers to javafx.fxml;
    opens ca.proj.Models to javafx.base;
    opens ca.proj.Utility to javafx.base;
}