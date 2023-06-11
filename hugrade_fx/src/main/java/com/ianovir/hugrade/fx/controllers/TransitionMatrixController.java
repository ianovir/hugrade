package com.ianovir.hugrade.fx.controllers;

import com.ianovir.hugrade.core.converters.TransMatrix2CsvConverter;
import com.ianovir.hugrade.core.converters.TransMatrix2TxtConverter;
import com.ianovir.hugrade.core.operators.GraphNormalizer;
import com.ianovir.hugrade.fx.views.GraphView;
import com.ianovir.hugrade.fx.views.NodeView;
import com.ianovir.hugrade.fx.views.TransitionMatrixView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TransitionMatrixController {

    public AnchorPane mainPane;
    public MenuItem miTxExportTxt;
    public MenuItem miNormalizeGraph;
    public MenuItem miNormalizeNode;
    public MenuItem miTxExportCsv;
    public TransitionMatrixView transMatrix;

    private GraphView graphView;
    private boolean initialized;

    public void setGraphView(GraphView gv){
        this.graphView = gv;
        updateTransitionMatrix();
        initialize();
    }

    private void initialize() {
        if(initialized) return;
        setupNormalizeButtons();
        setupExportButtons();
        initialized = true;
    }

    private void setupExportButtons() {
        miTxExportTxt.setOnAction(event -> {
            String res = TransMatrix2TxtConverter.convert(graphView.getGraph());

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
            if (file != null) writeToFile(res, file);

        });

        miTxExportCsv.setOnAction(event -> {
            String res = TransMatrix2CsvConverter.doWork(graphView.getGraph());

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
            if (file != null) writeToFile(res, file);
        });
    }

    private void setupNormalizeButtons() {
        miNormalizeNode.setOnAction(a-> {
            normalizeSelectedNodeEdges();
        });

        miNormalizeGraph.setOnAction(a-> {
            GraphNormalizer gn = new GraphNormalizer();
            gn.normalize(graphView.getGraph());
            refreshUI();
        });
    }

    private void refreshUI() {
        graphView.refreshEdges();
        transMatrix.forceUpdate();
    }

    private void normalizeSelectedNodeEdges() {
        if(graphView.getSelectedNodes().size()<=0) return;
        for(NodeView nv : graphView.getSelectedNodes()){
            graphView.getGraph().stochasticNormalizeNodeEdges(nv.getGNode());
        }
        refreshUI();
    }

    private void writeToFile(String res, File file) {
        try {
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(res);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTransitionMatrix() {
        if(graphView!=null){
            transMatrix.setGraphView(graphView);
        }
    }

    public TransitionMatrixView getTransMatrix() {
        return transMatrix;
    }
}
