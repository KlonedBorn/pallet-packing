module BinPackerApp {
    requires javafx.fxml;
    requires javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;

    opens edu.uwi.soscai.controller to javafx.fxml;
    opens edu.uwi.soscai to javafx.fxml;

    exports edu.uwi.soscai;
    exports edu.uwi.soscai.controller;
}
