package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.core.business.GraphChangeObserver;
import com.ianovir.hugrade.presentation.utils.HugradeSettings;
import com.ianovir.hugrade.presentation.utils.WindowsLauncher;
import com.ianovir.hugrade.presentation.views.GraphView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ToolBarController {
    private HugradeSettings settings;
    private GraphChangeObserver observer;
    public ImageView btnMagnetGrid;
    public ImageView btnTransView;
    public ImageView btnNodeValues;
    public ImageView btnRemoveZeroEdges;
    public AnchorPane mainPane;

    public void init(HugradeSettings settings, GraphChangeObserver graphChangeObserver){
        this.settings = settings;
        this.observer = graphChangeObserver;
        initButtonsActions();
    }

    private void initButtonsActions() {
        btnMagnetGrid.setOnMouseClicked(event -> toggleMagnetGrid());
        btnTransView.setOnMouseClicked(event -> launchTxMatrix());
        btnNodeValues.setOnMouseClicked(event -> toggleShowNodeValues());
        btnRemoveZeroEdges.setOnMouseClicked(event -> removeZeroEdges());
    }

    private void launchTxMatrix() {
        WindowsLauncher.launchTransMxWindow(this, settings.getGraphView(), observer);
    }

    private void removeZeroEdges() {
        GraphView gv = settings.getGraphView();
        if(gv!=null){
            gv.clearZeroEdges();
            observer.onGraphChanged(gv);
        }
    }

    private void toggleShowNodeValues() {
        settings.setNodeValuesVisible(!settings.areNodeValuesVisible());
        observer.onGraphChanged(settings.getGraphView());
    }

    private void toggleMagnetGrid() {
        settings.setMagnetGrid(!settings.isMagnetGridOn());
        observer.onGraphChanged(settings.getGraphView());
    }


}
