package com.ianovir.hugrade.presentation.views;

import com.ianovir.hugrade.core.business.GraphChangeObserver;
import com.ianovir.hugrade.core.business.converters.Graph2TransMatrixConverter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

/**
 * Custom JFX view for the transition matrix of a {@link com.ianovir.hugrade.core.models.Graph}
 */
public class TransitionMatrixView extends AnchorPane {

    private GraphView graphView;
    private final TableView<String[]> transMatrixTable;
    private final ArrayList<GraphChangeObserver> graphChangeObservers;
    private final ObservableList<String[]> data;

    public TransitionMatrixView() {
        super();

        data = FXCollections.observableArrayList();
        graphChangeObservers = new ArrayList<>();
        transMatrixTable = new TableView<>();

        setupTransitionMatrixTable();
        setMatrixTableAnchors();
        getChildren().addAll(transMatrixTable);
    }

    private void setMatrixTableAnchors() {
        AnchorPane.setBottomAnchor(transMatrixTable, 0.0);
        AnchorPane.setTopAnchor(transMatrixTable, 0.0);
        AnchorPane.setLeftAnchor(transMatrixTable, 0.0);
        AnchorPane.setRightAnchor(transMatrixTable, 0.0);
    }

    private void setupTransitionMatrixTable() {
        transMatrixTable.setEditable(true);

        //select node on row click
        transMatrixTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> graphView.selectNodeById(
                    transMatrixTable.getSelectionModel().getSelectedIndex())
            );
            return row;
        });
    }

    public void setGraphView(GraphView graphView){
        this.graphView = graphView;
        forceUpdate();
    }

    public void forceUpdate() {
        prepareData();
        refreshTableColumns();
        transMatrixTable.setItems(data);
    }

    private void refreshTableColumns() {
        transMatrixTable.getColumns().clear();
        for (int col = 0; col < data.size()+1; col++) {
            TableColumn<String[], String> newTableColumn = buildTableColumn(col);
            boolean isFirstColumn = col==0;
            newTableColumn.setEditable(!isFirstColumn);
            if(!isFirstColumn){
                newTableColumn.setOnEditCommit(this::onEditValueCell);
            }
            transMatrixTable.getColumns().add(newTableColumn);
        }
    }

    private void onEditValueCell(TableColumn.CellEditEvent<String[], String> event) {
        int c = event.getTablePosition().getColumn();
        if(c==0) return;
        int r = event.getTablePosition().getRow();
        float newVal = parseEventValue(event);
        updateGraphValue(c, r, newVal);
        event.consume();
    }

    private void updateGraphValue(int c, int r, float newVal) {
        graphView.updateOrCreateEdge(r, c -1, newVal);
        updateData(r, c -1, newVal);
        prepareData();
        notifyObservers();
    }

    private void updateData(int src, int dst, float newVal) {
        data.get(src)[dst] = String.valueOf(newVal);
    }

    private float parseEventValue(TableColumn.CellEditEvent<String[], String> event) {
        float newVal = 0;
        try{
            newVal = Float.parseFloat(event.getNewValue());
        }catch(NumberFormatException ignored){
            //TODO: prompt error!
        }
        return newVal;
    }

    private void notifyObservers() {
        //TODO: detail event
        for(GraphChangeObserver obs : graphChangeObservers)
            obs.onGraphChanged(graphView);
    }

    private TableColumn<String[], String> buildTableColumn(int columnIndex) {
        String colName = "";
        if(columnIndex > 0){
            var node = graphView.getGraph().getNodes().get(columnIndex-1);
            colName = node.getName();
        }
        TableColumn<String[], String> result = new TableColumn<>(colName);
        result.setSortable(false);
        result.setEditable(true);
        result.setCellFactory(TextFieldTableCell.forTableColumn());

        int finalCol = columnIndex;
        result.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[finalCol]));

        return result;

    }

    private void prepareData() {
        data.clear();
        float[][] matrix = Graph2TransMatrixConverter.convert(graphView.getGraph());

        for(int n=0; n<matrix.length; n++){
            float[] nodeRow = matrix[n];
            String[] nodesMap = new String[nodeRow.length+1];
            nodesMap[0] = getNodeNameByIndex(n);
            for(int i = 1; i<nodesMap.length ;i++){
                nodesMap[i] = String.valueOf(nodeRow[i-1]);
            }
            data.add(nodesMap);
        }
    }

    private String getNodeNameByIndex(int n) {
        return graphView.getGraph().getNodes().get(n).getName();
    }

    public void addChangeObserver(GraphChangeObserver obs){
        if(!graphChangeObservers.contains(obs))
            graphChangeObservers.add(obs);
    }

    public void removeChangeObserver(GraphChangeObserver obs){
        graphChangeObservers.remove(obs);
    }




}
