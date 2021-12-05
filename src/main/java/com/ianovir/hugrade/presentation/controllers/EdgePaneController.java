package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.presentation.views.EdgeView;
import com.ianovir.hugrade.presentation.views.GraphView;
import com.ianovir.hugrade.presentation.views.NodeView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.text.DecimalFormat;

import static java.lang.Float.parseFloat;

public class EdgePaneController {

    public ComboBox<GNode> cbOrigin;
    public ComboBox<GNode> cbDestination;
    public Label lblSource;
    public Label lblDestination;
    public TextField tfWeight;
    public TextArea taDescription;
    public Label muLbl;
    public Button btnSwap;
    public AnchorPane mainPane;

    private EdgeView edgeView;

    private final DecimalFormat df = new DecimalFormat("#.###");
    private GEdge edge;
    private Graph graph;

    @FXML
    public void initialize(){
        setWeightValueChangeListener();
    }

    private void setWeightValueChangeListener() {
        tfWeight.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                tryUpdateNewWeight(newValue);
            }catch(NumberFormatException ex){
                tfWeight.setText(newValue.equals("-")? "-1": "1");
            }
        });
    }

    private void tryUpdateNewWeight(String newValue) {
        float v = parseFloat(newValue);
        edgeView.setWeight(v);
    }

    public void setEdge(EdgeView e, GraphView graphView){
        this.graph = graphView.getGraph();
        this.edgeView = e;
        this.edge = e.getEdge();

        muLbl.setText(graph.getEdgeMu());

        setupWeightText();
        updateOriginComboBox();
        updateDestinationComboBox();
        setupExtremeComboBoxes(graphView);

        btnSwap.setOnAction(event -> swapExtremes());
    }

    private void setupWeightText() {
        tfWeight.setText( df.format(edgeView.getWeight()));
        tfWeight.textProperty().addListener((obs, ov, newValue) -> edgeView.setWeight(parseFloat(newValue))
        );
    }

    private void setupExtremeComboBoxes(GraphView graphView) {
        cbDestination.valueProperty().addListener((obs, ov, newValue) -> onChangeDestinationComboBox(graphView, newValue));
        cbOrigin.valueProperty().addListener((obs, ov, newValue) -> OnChangeSourceComboBox(graphView, newValue));
    }

    private void OnChangeSourceComboBox(GraphView graphView, GNode newValue) {
        if(newValue ==null) return;
        int nodeId = graph.getNodeId(newValue);
        if(!edgeExists(nodeId, edge.getDestination())){
            this.edgeView.setOrigin(graphView.getNodeViewById(nodeId));
            updateDestinationComboBox();
            edgeView.refresh();
        }else{
            System.err.printf("Edge %d->%d already exists%n",nodeId, edgeView.getEdge().getSource());
        }
    }

    private void onChangeDestinationComboBox(GraphView graphView, GNode newValue) {
        if(newValue ==null) return;
        int nodeId = graph.getNodeId(newValue);
        if (!edgeExists(edge.getSource(), nodeId )) {
            this.edgeView.setDestination(graphView.getNodeViewById(nodeId));
            updateOriginComboBox();
            edgeView.refresh();
        } else {
            System.err.printf("Edge %d->%d already exists%n",edgeView.getEdge().getSource(), nodeId);
        }
    }

    private void swapExtremes() {
        edgeView.swapExtremes();
        updateDestinationComboBox();
        updateOriginComboBox();
    }

    private void updateDestinationComboBox() {
        cbDestination.getItems().clear();
        cbDestination.setItems(FXCollections.observableArrayList(graph.getNodes()));
        cbDestination.getSelectionModel().select(graph.getNodeById(edge.getDestination()));
    }

    private void updateOriginComboBox() {
        cbOrigin.getItems().clear();
        cbOrigin.setItems(FXCollections.observableArrayList(graph.getNodes()));
        cbOrigin.getSelectionModel().select(graph.getNodeById(edge.getSource()));
    }

    private boolean edgeExists(int source, int destination) {
        return graph.edgeExists(source, destination);
    }


}
