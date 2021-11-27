package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.NodeView;
import com.ianovir.hugrade.views.GraphView;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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
    public AnchorPane mainPane;

    private NodeView node;
    private GraphView graphView;
    private static Color lastColor = Color.ORANGE;
    private boolean renamingContinues;

    public void setNode(NodeView n, GraphView graphV){
        this.node = n;
        this.graphView = graphV;
        renamingContinues = false;

        setupLabels();

        setupEdgesList();
        setupTextFieldName();
        setupTextFieldValue();
        setupTextAreaDescription();
        setupColorPicker();
        setupSetZeroButton();

        valueGroup.setVisible(graphView.getGraph().showNodeValues());
    }

    private void setupLabels() {
        labelId.setText(String.valueOf(getNodeId()));
        muLbl.setText(graphView.getGraph().getNodeMu());
    }

    private void setupSetZeroButton() {
        btnSetZero.setOnAction(event -> {
                    graphView.getGraph().changeNodeID(getNodeId(), 0);
                    labelId.setText(String.valueOf(getNodeId()));
                }
        );
    }

    private void setupColorPicker() {
        colPicker.setValue(node.getColor());
        colPicker.setOnAction(t -> {
            node.setColor(colPicker.getValue());
            lastColor = colPicker.getValue();
        });
    }

    private void setupTextAreaDescription() {
        taDescription.setText(node.getGNode().getDescription());
        taDescription.textProperty().addListener((observable, oldValue, newValue) ->
                node.getGNode().setDescription(newValue));
    }

    private int getNodeId() {
        return graphView.getGraph().getNodeId(node.getGNode());
    }

    private void setupTextFieldValue() {
        tfValue.setText(String.format("%.1f", node.getGNode().getValue()));
        tfValue.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                float v = Float.parseFloat(newValue);
                node.setValue(v);
            }catch(NumberFormatException ex){
                node.setValue(0f);
            }
        });
    }

    private void setupTextFieldName() {
        tfName.setText(node.getGNode().getName());
        tfName.textProperty().addListener((observable, oldValue, newValue) ->
                node.setName(newValue)
        );
    }

    private void setupEdgesList() {
        TableColumn<EdgeView, String> fromColumn = new TableColumn<>("From");
        fromColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell call(final TableColumn<EdgeView, String> param) {
                final TableCell<EdgeView, String> cell = new TableCell<>() {
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
        toColumn.setCellFactory(new Callback<>() {
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
        weightColumn.setCellFactory(new Callback<>() {
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

    public void onKeyPress(KeyEvent e) {
        if (e.getCode().isDigitKey() || e.getCode().isLetterKey()) {
            renameNode(e);
        }else
        if (e.getCode() == KeyCode.BACK_SPACE) {
            backSpaceNodeName();
        }
    }

    private void renameNode(javafx.scene.input.KeyEvent e) {
        if(renamingContinues){
            tfName.setText(tfName.getText() + e.getText());
        }else{
            tfName.setText(e.getText());
            renamingContinues = true;
        }
    }

    private void backSpaceNodeName() {
        if(renamingContinues){
            int end = tfName.getLength() - 2;
            if(end>0) tfName.setText(tfName.getText(0, end));
        }else{
            tfName.setText("");
            renamingContinues = true;
        }
    }

}
