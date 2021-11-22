package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.core.business.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.business.solvers.path.PathSolver;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.GraphView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathSolverController {

    public ComboBox<PathSolver.BidirectionalConnectionOp> cbBidConn;
    public ComboBox<PathSolver.NegativeEdgesOp> cbNegEdges;
    public ComboBox<GNode> cbOrigin;
    public ComboBox<GNode> cbDestination;
    public Button btnSolve;
    public TableView<Integer> tablePath;
    public Button btnExport;
    public ProgressBar bar;

    private GraphView graphView;
    private Graph graph;

    public void init(GraphView graphView, PathSolver solver){
        if(graphView==null) return;

        this.graphView = graphView;
        graph = graphView.getGraph();

        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        bar.setVisible(false);

        //TODO: implement export
        btnExport.setDisable(true);

        cbOrigin.setItems(FXCollections.observableArrayList(graph.getNodes()));
        cbDestination.setItems(FXCollections.observableArrayList(graph.getNodes()));

        btnSolve.setOnAction(event -> actionSolve(graphView, solver));

        cbBidConn.getItems().setAll(PathSolver.BidirectionalConnectionOp.values());
        cbNegEdges.getItems().setAll(PathSolver.NegativeEdgesOp.values());

        cbBidConn.getSelectionModel().select(0);
        cbNegEdges.getSelectionModel().select(0);
    }

    private void actionSolve(GraphView graphView, PathSolver solver) {
        graphView.clearSelection();
        tablePath.getItems().clear();

        if(cbOrigin.getSelectionModel().getSelectedItem()==null ||
                cbDestination.getSelectionModel().getSelectedItem()==null) return;

        GNode srcNode = cbOrigin.getSelectionModel().getSelectedItem();
        GNode dstNode = cbDestination.getSelectionModel().getSelectedItem();

        int src = graph.getNodeId(srcNode);
        int dst = graph.getNodeId(dstNode);

        Task<int[]> task = prepareExecutionTask(graphView, solver, src, dst);

        bar.setVisible(true);
        new Thread(task).start();
    }

    private Task<int[]> prepareExecutionTask(GraphView graphView, PathSolver solver, int src, int dst) {
        Task<int[]> task = new Task<>() {
            @Override
            public int[] call() {
                return solver.solve(src, dst,
                        new GraphSolverSettings(
                                cbBidConn.getSelectionModel().getSelectedItem(),
                                cbNegEdges.getSelectionModel().getSelectedItem()
                        )
                );
            }
        };

        task.setOnSucceeded(e -> {
            bar.setVisible(false);
            int[] res = task.getValue();
            if(res!=null){
                updateMatrix(res, solver.getCosts());
                graphView.selectNodes(res);
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Something gone wrong...");
                alert.setHeaderText("Result is null");
                alert.setContentText("It looks like there is no optimal path between the selected nodes.");
            }
        });
        return task;
    }

    private void updateMatrix(int[] res, float[][] costs) {
        tablePath.getColumns().clear();
        List<Integer> boxedRes = Arrays.stream(res).boxed().collect(Collectors.toList());

        TableColumn<Integer, String> idColumn = setupIdColumn();
        TableColumn<Integer, String> nodeColumn = setupNodeColumn();
        TableColumn<Integer, String> partialCostColumn = setupPartialCostColumn(costs, boxedRes);
        TableColumn<Integer, String> totalCostColumn = setupTotalCostColumn(costs, boxedRes);
        setupRowFactory();

        tablePath.setItems(FXCollections.observableArrayList(boxedRes));
        tablePath.getColumns().addAll(idColumn, nodeColumn, partialCostColumn, totalCostColumn);
    }

    private void setupRowFactory() {
        tablePath.setRowFactory(tv -> {
            TableRow<Integer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    graphView.selectNodeById(row.getItem());
                }
            });
            return row ;
        });
    }

    private TableColumn<Integer, String> setupTotalCostColumn(float[][] costs, List<Integer> boxedRes) {
        TableColumn<Integer, String> totalCostColumn = new TableColumn<>("Total cost");
        totalCostColumn.setCellValueFactory(param -> new SimpleStringProperty(
                String.valueOf(costs[boxedRes.indexOf(param.getValue())][1]))

        );
        totalCostColumn.setEditable(false);
        totalCostColumn.setSortable(false);
        return totalCostColumn;
    }

    private TableColumn<Integer, String> setupPartialCostColumn(float[][] costs, List<Integer> boxedRes) {
        TableColumn<Integer, String> partialCostColumn = new TableColumn<>("Partial cost");
        partialCostColumn.setCellValueFactory(param -> new SimpleStringProperty(
                String.valueOf(costs[boxedRes.indexOf(param.getValue())][0]))
        );
        partialCostColumn.setEditable(false);
        partialCostColumn.setSortable(false);
        return partialCostColumn;
    }

    private TableColumn<Integer, String> setupNodeColumn() {
        TableColumn<Integer, String> nodeColumn = new TableColumn<>("Node");
        nodeColumn.setCellValueFactory(param -> new SimpleStringProperty(graph.getNodeById(param.getValue()).getName()));
        nodeColumn.setEditable(false);
        nodeColumn.setSortable(false);
        return nodeColumn;
    }

    private TableColumn<Integer, String> setupIdColumn() {
        TableColumn<Integer, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));
        idColumn.setEditable(false);
        idColumn.setSortable(false);
        return idColumn;
    }

    public void selectNegativeEdgesItem(PathSolver.NegativeEdgesOp op){
        cbNegEdges.getSelectionModel().select(op);
    }

    public void selectBidirectionalConnections(PathSolver.BidirectionalConnectionOp op){
        cbBidConn.getSelectionModel().select(op);
    }

    public void setNegativeEdgesDisabled(boolean disable){
        cbNegEdges.setDisable(disable);
    }

    public void setBidirectionalConnectionsDisabled(boolean disable){
        cbBidConn.setDisable(disable);
    }

}
