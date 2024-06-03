package edu.uwi.soscai;

import java.net.URL;

import org.kordamp.bootstrapfx.BootstrapFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends javafx.application.Application {

    private static final String VERSION = "0.1.2";

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(FXML_PATH));
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.setTitle("Bin Packer " + VERSION);
        stage.show();
    }

    public static void main(String[] args) {
        App.launch(args);
    }

    public static final URL FXML_PATH = App.class.getResource("view/index.fxml");
}
