package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.GraphView;
import com.ianovir.hugrade.views.NodeView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DecimalFormat;

public class EdgePaneController {

    public ComboBox<GNode> cbOrigin;
    public ComboBox<GNode> cbDestination;
    public Label lblSource;
    public Label lblDestination;
    public TextField tfWeight;
    public TextArea taDescription;
    public Label muLbl;
    public Button btnSwap;

    private EdgeView edgeView;

    private final DecimalFormat df = new DecimalFormat("#.###");
    private GEdge edge;
    private Graph graph;

    @FXML
    public void initialize(){
        tfWeight.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                float v = Float.parseFloat(newValue);
                edgeView.setWeight(v);
            }catch(NumberFormatException ex){
                tfWeight.setText(newValue.equals("-")? "-1": "1");
            }
        });
    }

    public void setEdge(EdgeView e, GraphView graphView){
        this.graph = graphView.getGraph();
        this.edgeView = e;
        this.edge = e.getEdge();

        tfWeight.setText( df.format(edgeView.getWeight()));
        tfWeight.textProperty().addListener((observable, oldValue, newValue) ->{
                edgeView.setWeight(Float.parseFloat(newValue));
            }
        );

        muLbl.setText(graph.getEdgeMu());

        updateOriginCombobox();
        updateDestinationCombobox();
        setupExtremeComboboxes(graphView);

        btnSwap.setOnAction(event -> {

            swapExtremes();
        });
    }

    private void setupExtremeComboboxes(GraphView graphView) {
        cbDestination.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null) {
                int nodeId = graph.getNodeId(newValue);
                if (!edgeExists(edge.getSource(), nodeId )) {
                    this.edgeView.setDestination(graphView.getNodeViewById(nodeId));
                    updateOriginCombobox();
                    edgeView.refresh();
                } else {
                    System.err.printf("Edge %d->%d already exists%n",
                            edgeView.getEdge().getSource(), nodeId);
                }
            }
        });

        cbOrigin.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                int nodeId = graph.getNodeId(newValue);
                if(!edgeExists(nodeId, edge.getDestination())){
                    this.edgeView.setOrigin(graphView.getNodeViewById(nodeId));
                    updateDestinationCombobox();
                    edgeView.refresh();
                }else{
                    System.err.printf("Edge %d->%d already exists%n",
                            nodeId, edgeView.getEdge().getSource());
                }
            }
        });
    }

    private void swapExtremes() {
        int dstId = graph.getNodeId(edgeView.getDestination().getGNode());
        int oriId = graph.getNodeId(edgeView.getOrigin().getGNode());

        if(!edgeExists(dstId,oriId)){
            NodeView dummy = new NodeView("", 0, 0);
            NodeView dst = edgeView.getDestination();
            NodeView src = edgeView.getOrigin();

            edgeView.setDestination(dummy);
            edgeView.setOrigin(dst);
            edgeView.setDestination(src);
            updateDestinationCombobox();
            updateOriginCombobox();
            edgeView.refresh();

        }else{
            System.out.println("Inverted edge already exists");
        }
    }

    private void updateDestinationCombobox() {
        cbDestination.getItems().clear();
        cbDestination.setItems(FXCollections.observableArrayList(graph.getNodes()));
        cbDestination.getSelectionModel().select(graph.getNodeById(edge.getDestination()));
    }

    /**
     * Updates the content of the origin combobox, avoiding the choice of creating existing edges
     */
    private void updateOriginCombobox() {
        cbOrigin.getItems().clear();
        cbOrigin.setItems(FXCollections.observableArrayList(graph.getNodes()));
        cbOrigin.getSelectionModel().select(graph.getNodeById(edge.getSource()));
    }

    private boolean edgeExists(int source, int destination) {
        return graph.edgeExists(source, destination);
    }

}
