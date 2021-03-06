package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.NodeView;
import com.ianovir.hugrade.views.GraphView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class NodePaneController {

    public Button btnSetZero;
    public TextField tfValue;
    public ColorPicker colPicker;
    public TextField tfName;
    public TextArea taDescription;
    public TableView<EdgeView> edgesTable;
    public Label labelId;
    public Label muLbl;
    public FlowPane valueGroup;

    private NodeView node;
    private GraphView graphView;
    private static Color lastColor = Color.ORANGE;

    public void setNode(NodeView n, GraphView graphV){
        this.node = n;
        this.graphView = graphV;

        labelId.setText(String.valueOf(node.getGNode().getId()));

        setupEdgesList();

        tfName.setText(node.getGNode().getName());
        tfName.textProperty().addListener((observable, oldValue, newValue) ->
                node.setName(newValue)
        );

        tfValue.setText(String.format("%.1f", node.getGNode().getValue()));
        tfValue.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                float v = Float.parseFloat(newValue);
                node.setValue(v);
            }catch(NumberFormatException ex){
                node.setValue(0f);
            }

        });

        taDescription.setText(node.getGNode().getDescription());
        taDescription.textProperty().addListener((observable, oldValue, newValue) ->
                node.getGNode().setDescription(newValue));

        colPicker.setValue(node.getColor());
        colPicker.setOnAction((EventHandler<ActionEvent>) t -> {
            node.setColor(colPicker.getValue());
            lastColor = colPicker.getValue();
        });

        btnSetZero.setOnAction(event -> {
                    graphView.getGraph().changeNodeID(node.getGNode().getId(), 0);
                    labelId.setText(String.valueOf(node.getGNode().getId()));
                }
        );

        muLbl.setText(graphView.getGraph().getNodeMu());
        valueGroup.setVisible(graphView.getGraph().showNodeValues());
    }

    /**
     * Configures the list of Edges belonging to the Node
     */
    private void setupEdgesList() {
        TableColumn<EdgeView, String> fromColumn = new TableColumn<>("From");
        fromColumn.setCellFactory(new Callback<TableColumn<EdgeView, String>, TableCell<EdgeView, String>>() {
            @Override
            public TableCell call(final TableColumn<EdgeView, String> param) {
                final TableCell<EdgeView, String> cell = new TableCell<EdgeView, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            EdgeView n = getTableView().getItems().get(getIndex());
                            setText(n.getOrigin().getGNode().getName());
                            setOnMouseClicked(event -> graphView.select(n));
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });

        TableColumn<EdgeView, String> toColumn = new TableColumn<>("To");
        toColumn.setCellFactory(new Callback<TableColumn<EdgeView, String>, TableCell<EdgeView, String>>() {
            @Override
            public TableCell call(final TableColumn<EdgeView, String> param) {
                final TableCell<EdgeView, String> cell = new TableCell<EdgeView, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            EdgeView n = getTableView().getItems().get(getIndex());
                            setText(n.getDestination().getGNode().getName());
                            setOnMouseClicked(event -> graphView.select(n));
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });

        TableColumn<EdgeView, String> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellFactory(new Callback<TableColumn<EdgeView, String>, TableCell<EdgeView, String>>() {
            @Override
            public TableCell call(final TableColumn<EdgeView, String> param) {
                final TableCell<EdgeView, String> cell = new TableCell<EdgeView, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            final EdgeView n = getTableView().getItems().get(getIndex());
                            setText(n.getEdge().getWeight() + "");
                            setGraphic(null);
                            setOnMouseClicked(event -> graphView.select(n));
                        }
                    }
                };
                return cell;
            }
        });

        edgesTable.setItems(FXCollections.observableArrayList(graphView.getEdgesByNode(node)));
        edgesTable.getColumns().addAll(fromColumn, toColumn, weightColumn);
    }

    public static Color getLastColor() {
        return lastColor;
    }

    public static void setLastColor(Color lastColor) {
        NodePaneController.lastColor = lastColor;
    }
}
