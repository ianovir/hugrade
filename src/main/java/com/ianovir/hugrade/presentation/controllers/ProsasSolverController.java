package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.core.business.solvers.ProsasSolver;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.presentation.views.GraphView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import java.util.Arrays;

public class ProsasSolverController {

    public Label lblZero;
    public ProgressBar bar;

    public Button btnSolve;
    public TableView<float[]> tvResult;
    public Button btnExport;

    private ObservableList<float[]> data;
    private GraphView graphView;

    public void setGraphView(GraphView graphView){
        this.graphView = graphView;
        data = FXCollections.observableArrayList();
        init();
    }

    public void init(){

        if(graphView==null || graphView.getGraph()==null){
            System.err.println("Input graph is null");
            return;
        }

        lblZero.setText("Starting node: " + graphView.getGraph().getNodeById(0).getName());

        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        bar.setVisible(false);

        //TODO: implement export
        btnExport.setDisable(true);

        btnSolve.setOnAction(event -> actionSolve());

    }

    private void actionSolve() {
        tvResult.getItems().clear();
        updateData();

        setupIdColumn();
        setupNameColumn();
        setupValueColumn();
        setupRowClickFactory();

        tvResult.setItems(data);
        tvResult.refresh();

        btnSolve.setDisable(true);
    }

    private void setupRowClickFactory() {
        tvResult.setRowFactory(tv -> {
            TableRow<float[]> row = new TableRow<>();
            row.setOnMouseClicked(e -> graphView.selectNodeById((int) tvResult.getSelectionModel().getSelectedItem()[0]));
            return row;
        });
    }

    private void setupValueColumn() {
        TableColumn<float[], String> valCol = new TableColumn<>("Probability");
        valCol.setSortable(false);
        valCol.setEditable(false);
        valCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue()[1])));
        tvResult.getColumns().add(valCol);
    }

    private void setupNameColumn() {
        TableColumn<float[], String> nameCol = new TableColumn<>("Name");
        nameCol.setSortable(false);
        nameCol.setEditable(false);
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(
                graphView.getGraph()
                        .getNodeById((int)param.getValue()[0])
                        .getName())
        );
        tvResult.getColumns().add(nameCol);
    }

    private void setupIdColumn() {
        TableColumn<float[], Integer> idCol = new TableColumn<>("ID");
        idCol.setSortable(false);
        idCol.setEditable(false);
        idCol.setCellValueFactory(param -> new SimpleIntegerProperty((int) param.getValue()[0]).asObject());
        tvResult.getColumns().add(idCol);
    }

    private void updateData() {
        data.clear();
        solve(graphView.getGraph());
    }

    private void solve(Graph graph) {
        Task<float[][]> task = new Task<>() {
            @Override
            public float[][] call() {
                return ProsasSolver.solve(graph);
            }
        };

        task.setOnSucceeded(event -> {
            onSolverSucceeded(task);
        });

        bar.setVisible(true);
        new Thread(task).start();
    }

    private void onSolverSucceeded(Task<float[][]> task) {
        bar.setVisible(false);
        float[][]  res = task.getValue();
        if(res!=null){
            roundResult(res);
            data.addAll(Arrays.asList(res));
        }
        System.out.println(Arrays.deepToString(res));
    }

    private void roundResult(float[][] res) {
        for(int i =0;i<res.length;i++){
            for(int j =0;j<res[i].length;j++) {
                res[i][j] = (float) Math.round(res[i][j] * 1000) / 1000;
                System.out.println(res[i][j]);
            }
        }
    }

}
