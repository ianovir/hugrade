package com.ianovir.hugrade.fx.controllers;

import com.ianovir.hugrade.fx.utils.HugradeSettings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SettingsController {

    public TextField tfNodesMu;
    public TextField tfEdgesMu;
    public CheckBox chbShowNodesValues;
    public CheckBox chbMagnetGrid;
    private HugradeSettings settings;

    public void init(HugradeSettings settings){
        this.settings = settings;
        setupNodeMuText();
        setupEdgeMuText();
        setupShowNodeValuesOption();
        setupMagnetGridOption();

    }

    private void setupMagnetGridOption() {
        chbMagnetGrid.setSelected(settings.isMagnetGridOn());
        chbMagnetGrid.selectedProperty().addListener((observable, oldValue, newValue) -> settings.setMagnetGrid(newValue));
    }

    private void setupShowNodeValuesOption() {
        chbShowNodesValues.setSelected(settings.showNodeValues());
        chbShowNodesValues.selectedProperty().addListener((observable, oldValue, newValue) -> settings.setNodeValuesVisible(newValue));
    }

    private void setupEdgeMuText() {
        tfEdgesMu.setText(settings.getEdgeMu());
        tfEdgesMu.textProperty().addListener((observable, oldValue, newValue) -> settings.setEdgeMu(newValue));
    }

    private void setupNodeMuText() {
        tfNodesMu.setText(settings.getNodeMu());
        tfNodesMu.textProperty().addListener((observable, oldValue, newValue) -> settings.setNodeMu(newValue));
    }

}
