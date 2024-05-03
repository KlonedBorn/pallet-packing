module edu.uwi.soscai {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.uwi.soscai to javafx.fxml;
    exports edu.uwi.soscai;
}
