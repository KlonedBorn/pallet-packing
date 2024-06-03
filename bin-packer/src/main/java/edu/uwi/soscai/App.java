package edu.uwi.soscai;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends javafx.application.Application {

    private static final String VERSION = "0.1.2";

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(FXML_PATH)));
        stage.setTitle("Bin Packer " + VERSION);
        stage.show();
    }

    public static void main(String[] args) {
        App.launch(args);
    }

    public static final URL FXML_PATH = App.class.getResource("view/index.fxml");
}
