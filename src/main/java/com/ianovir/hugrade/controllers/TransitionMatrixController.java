package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.core.business.converters.TransMatrix2CsvConverter;
import com.ianovir.hugrade.core.business.converters.TransMatrix2TxtConverter;
import com.ianovir.hugrade.core.business.operators.GraphNormalizer;
import com.ianovir.hugrade.views.GraphView;
import com.ianovir.hugrade.views.NodeView;
import com.ianovir.hugrade.views.TransitionMatrixView;
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
            if(graphView.getSelectedNodes().size()>0){
                for(NodeView nv : graphView.getSelectedNodes()){
                    graphView.getGraph().normalizeNode(nv.getGNode());
                }
                graphView.refreshEdges();
                transMatrix.forceUpdate();
            }
        });

        miNormalizeGraph.setOnAction(a-> {
            GraphNormalizer gn = new GraphNormalizer();
            gn.doWork(graphView.getGraph());
            graphView.refreshEdges();
            transMatrix.forceUpdate();
        });
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
