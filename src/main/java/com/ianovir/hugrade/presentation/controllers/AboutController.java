package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.Main;
import javafx.application.Application;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class AboutController {
    public Label lblVersion;
    public Hyperlink lblMainLink;

    public void init( Application app){
        lblMainLink.setOnAction(t -> app.getHostServices().showDocument("https://ianovir.com/hugrade/"));
        lblVersion.setText("v." + Main.UGLY_VERSION);
    }
}
