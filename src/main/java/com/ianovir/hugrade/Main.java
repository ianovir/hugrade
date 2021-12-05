package com.ianovir.hugrade;

import com.ianovir.hugrade.presentation.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{

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


        mainController.init(this);
    }

    public static String getVersion() {
        return "1.0.3";
    }

    public static void main(String[] args) {
        launch(args);
    }

}
