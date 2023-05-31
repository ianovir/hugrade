package com.ianovir.hugrade.fx.controllers;


import com.ianovir.hugrade.core.converters.Graph2NodesMatrixConverter;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.fx.GraphChangeObserver;
import com.ianovir.hugrade.fx.views.GraphView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.Collections;

public class NodesMatrixController {

    public TableView<String[]> nodesMatrix;
    public MenuItem miTxExportTxt;
    public MenuItem miTxExportCsv;
    public MenuItem miNormalizeGraph;
    public MenuItem miNormalizeNode;

    private ObservableList<String[]> data;
    private GraphView graphView;
    private ArrayList<GraphChangeObserver> graphChangeObservers;

    public void setGraphView(GraphView gv){
        this.graphView = gv;
        data = FXCollections.observableArrayList();
        graphChangeObservers = new ArrayList<>();
        nodesMatrix.setEditable(true);
        initialize();
    }

    private void initialize() {
        updateData();

        nodesMatrix.getColumns().clear();

        //ID column
        TableColumn<String[], String> idCol = new TableColumn<>("ID");
        idCol.setSortable(false);
        idCol.setEditable(false);
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[0]));
        nodesMatrix.getColumns().add(idCol);

        //name column
        TableColumn<String[], String> nameCol = new TableColumn<>("Name");
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> {
            int r = event.getTablePosition().getRow();
            graphView.getGraph().getNodeById(r).setName(event.getNewValue());
            onUpdate();
            event.consume();
        });
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[1]));
        nodesMatrix.getColumns().add(nameCol);

        //value column
        TableColumn<String[], String> valCol = new TableColumn<>("Value");
        valCol.setSortable(false);
        valCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valCol.setOnEditCommit(event -> {
            GNode node = graphView.getGraph().getNodeById(event.getTablePosition().getRow());
            float val = node.getValue();
            try{
                val = Float.parseFloat(event.getNewValue());
            }catch(NumberFormatException ignored){}
            node.setValue(val);
            onUpdate();
            event.consume();
        });
        valCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[2]));
        nodesMatrix.getColumns().add(valCol);

        nodesMatrix.setItems(data);
        nodesMatrix.refresh();
    }

    private void onUpdate() {
        updateData();
        for(GraphChangeObserver obs : graphChangeObservers) obs.onGraphChanged(graphView);
    }

    private void updateData() {
        data.clear();
        String[][] res = Graph2NodesMatrixConverter.convert(graphView.getGraph());
        Collections.addAll(data, res);
    }
    public void addChangeObserver(GraphChangeObserver obs){
        if(!graphChangeObservers.contains(obs))
            graphChangeObservers.add(obs);
    }

    public void removeChangeObserver(GraphChangeObserver obs){
        graphChangeObservers.remove(obs);
    }



}
