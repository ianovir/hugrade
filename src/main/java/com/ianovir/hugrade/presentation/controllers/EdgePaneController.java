package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.presentation.views.EdgeView;
import com.ianovir.hugrade.presentation.views.GraphView;
import javafx.beans.value.ChangeListener;
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
    private GraphView graphView;
    private ChangeListener<? super GNode> destinationComboBoxListener;
    private ChangeListener<? super GNode> originComboBoxListener;

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
        this.graphView = graphView;
        this.edgeView = e;
        this.edge = e.getEdge();

        muLbl.setText(getGraph().getEdgeMu());
        btnSwap.setOnAction(event -> swapExtremes());

        setupWeightText();
        initComboBoxes();

    }

    private void initComboBoxes() {
        updateOriginComboBox();
        updateDestinationComboBox();
        initComboBoxListeners();
        addExtremesComboBoxListeners();
    }

    private void initComboBoxListeners() {
        destinationComboBoxListener = (obs, ov, newValue) -> onChangeDestinationComboBox(newValue);
        originComboBoxListener = (obs, ov, newValue) -> OnChangeSourceComboBox(newValue);
    }

    private Graph getGraph() {
        return graphView.getGraph();
    }

    private void setupWeightText() {
        tfWeight.setText( df.format(edgeView.getWeight()));
        tfWeight.textProperty().addListener((obs, ov, newValue) -> edgeView.setWeight(parseFloat(newValue))
        );
    }

    private void addExtremesComboBoxListeners() {
        cbDestination.valueProperty().addListener(destinationComboBoxListener);
        cbOrigin.valueProperty().addListener(originComboBoxListener);
    }

    private void removeExtremesComboBoxListeners(){
        cbOrigin.valueProperty().removeListener(originComboBoxListener);
        cbDestination.valueProperty().removeListener(destinationComboBoxListener);
    }

    private void OnChangeSourceComboBox(GNode newValue) {
        if(newValue ==null) return;
        int nodeId = getGraph().getNodeId(newValue);
        if(!edgeExists(nodeId, edge.getDestination())){
            this.edgeView.setOrigin(graphView.getNodeViewById(nodeId));
            updateDestinationComboBox();
            edgeView.refresh();
        }else{
            System.err.printf("Edge %d->%d already exists%n",nodeId, edgeView.getEdge().getSource());
        }
    }

    private void onChangeDestinationComboBox(GNode newValue) {
        if(newValue ==null) return;
        int nodeId = getGraph().getNodeId(newValue);
        if (!edgeExists(edge.getSource(), nodeId )) {
            this.edgeView.setDestination(graphView.getNodeViewById(nodeId));
            updateOriginComboBox();
            edgeView.refresh();
        } else {
            System.err.printf("Edge %d->%d already exists%n",edgeView.getEdge().getSource(), nodeId);
        }
    }

    private void swapExtremes() {
        boolean swapOk = edgeView.swapExtremes();
        if(swapOk){
            removeExtremesComboBoxListeners();
            updateDestinationComboBox();
            updateOriginComboBox();
            addExtremesComboBoxListeners();
        }
    }

    private void updateDestinationComboBox() {
        cbDestination.getItems().clear();
        cbDestination.setItems(FXCollections.observableArrayList(getGraph().getNodes()));
        cbDestination.getSelectionModel().select(getGraph().getNodeById(edge.getDestination()));
    }

    private void updateOriginComboBox() {
        cbOrigin.getItems().clear();
        cbOrigin.setItems(FXCollections.observableArrayList(getGraph().getNodes()));
        cbOrigin.getSelectionModel().select(getGraph().getNodeById(edge.getSource()));
    }

    private boolean edgeExists(int source, int destination) {
        return getGraph().edgeExists(source, destination);
    }

}
