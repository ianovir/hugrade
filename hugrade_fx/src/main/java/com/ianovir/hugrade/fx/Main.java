package com.ianovir.hugrade.fx;

import com.ianovir.hugrade.data.GraphFileReaderWriter;
import com.ianovir.hugrade.fx.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        setupStage(primaryStage);
        var graphIo = new GraphFileReaderWriter();
        mainController.init(this, graphIo, graphIo);
    }

    private void setupStage(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        Parent root = loader.load();
        mainController = loader.getController();

        primaryStage.setResizable(true);
        Scene scene = new Scene(root, 800,600);

        primaryStage.setOnCloseRequest(event -> mainController.close(event));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hugrade");
        primaryStage.show();
    }

    public static String getVersion() {
        return "1.0.5";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
