package com.ianovir.hugrade.views;

import com.ianovir.hugrade.core.business.converters.Graph2TransMatrixConverter;
import com.ianovir.hugrade.core.models.GEdge;
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
        transMatrixTable = new TableView<>();
        transMatrixTable.setEditable(true);
        data = FXCollections.observableArrayList();
        graphChangeObservers = new ArrayList<>();

        //select node on row click
        transMatrixTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> graphView.selectNodeById( transMatrixTable.getSelectionModel().getSelectedIndex()));
            return row;
        });

        AnchorPane.setBottomAnchor(transMatrixTable, 0.0);
        AnchorPane.setTopAnchor(transMatrixTable, 0.0);
        AnchorPane.setLeftAnchor(transMatrixTable, 0.0);
        AnchorPane.setRightAnchor(transMatrixTable, 0.0);

        getChildren().addAll(transMatrixTable);
    }

    public void setGraphView(GraphView graphView){
        this.graphView = graphView;
        forceUpdate();
    }

    public void forceUpdate() {
        transMatrixTable.getColumns().clear();

        updateData();

        //define columns
        for (int col = 0; col < data.size()+1; col++) {
            TableColumn<String[], String> tc =
                    new TableColumn(col==0?"": graphView.getGraph().getNodes().get(col-1).getName());
            tc.setSortable(false);
            tc.setEditable(true);
            tc.setCellFactory(TextFieldTableCell.forTableColumn());
            tc.setOnEditCommit(event -> {
                float newVal = 0;
                try{
                    newVal = Float.parseFloat(event.getNewValue());
                }catch(NumberFormatException ignored){}

                int r = event.getTablePosition().getRow();
                int c = event.getTablePosition().getColumn();
                data.get(r)[c-1] = String.valueOf(newVal);

                if(c>0){
                    GEdge edge = graphView.getGraph().getEdgesByNodeIDs(r, c-1);
                    if(edge!=null) {
                        edge.setWeight(newVal);
                    }else{
                        //creating new edge
                        graphView.addEdges(new EdgeView(graphView.getNodeById(r),
                                graphView.getNodeById(c-1),
                                new GEdge(newVal, r, c-1)));
                    }
                    updateData();
                    for(GraphChangeObserver obs : graphChangeObservers) obs.onGraphChanged(graphView);
                }
                event.consume();
            });
            int finalCol = col;
            tc.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[finalCol]));
            transMatrixTable.getColumns().add(tc);
        }

        transMatrixTable.setItems(data);

    }

    private void updateData() {
        data.clear();

        //prepare data with row headers
        float[][] res = Graph2TransMatrixConverter.convert(graphView.getGraph());
        for(int n=0; n<res.length; n++){
            float[] nodeRow = res[n];
            String[] nodesMap = new String[nodeRow.length+1];
            nodesMap[0] = graphView.getGraph().getNodes().get(n).getName();
            for(int i = 1; i<nodesMap.length ;i++){
                nodesMap[i] =  nodeRow[i-1]+"";
            }
            data.add(nodesMap);
        }
    }

    public void addChangeObserver(GraphChangeObserver obs){
        if(!graphChangeObservers.contains(obs))
            graphChangeObservers.add(obs);
    }

    public void removeChangeObserver(GraphChangeObserver obs){
        graphChangeObservers.remove(obs);
    }

    public interface GraphChangeObserver {
        void onGraphChanged(GraphView gv);
    }


}
