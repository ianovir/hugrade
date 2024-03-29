package com.ianovir.hugrade.fx.controllers;

import com.ianovir.hugrade.fx.GraphChangeObserver;
import com.ianovir.hugrade.fx.utils.HugradeSettings;
import com.ianovir.hugrade.fx.utils.WindowsLauncher;
import com.ianovir.hugrade.fx.views.GraphView;
import javafx.scene.control.Tooltip;
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
        createButtonsToolTips();
    }

    private void createButtonsToolTips() {
        Tooltip.install(btnMagnetGrid, new Tooltip("Toggle magnet grid"));
        Tooltip.install(btnTransView, new Tooltip("Open Transition matrix"));
        Tooltip.install(btnNodeValues, new Tooltip("Show/Hide node values"));
        Tooltip.install(btnRemoveZeroEdges, new Tooltip("Delete zero-weighted edges"));
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
