package com.ianovir.hugrade;

import com.ianovir.hugrade.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private MainController mainController;
    public static String UGLY_VERSION = "1.0.2";

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

    public static void main(String[] args) {
        launch(args);
    }

}
