package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.core.models.Graph;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class GraphSettingsController {

    public TextField tfNodesMu;
    public TextField tfEdgesMu;
    public CheckBox chbShowNodesVals;
    private Graph graph;

    public void init(Graph graph){
        this.graph = graph;

        tfNodesMu.setText(graph.getNodeMu());
        tfEdgesMu.setText(graph.getEdgeMu());

        tfNodesMu.textProperty().addListener((observable, oldValue, newValue) -> {
            graph.setNodeMu(newValue);
        });

        tfEdgesMu.textProperty().addListener((observable, oldValue, newValue) -> {
            graph.setEdgeMu(newValue);
        });

        chbShowNodesVals.setSelected(graph.showNodeValues());
        chbShowNodesVals.selectedProperty().addListener((observable, oldValue, newValue) -> graph.setNodeValuesVisible(newValue));

    }

}
