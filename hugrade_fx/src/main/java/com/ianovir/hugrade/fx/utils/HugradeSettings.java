package com.ianovir.hugrade.fx.utils;

import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.fx.views.GraphView;

public class HugradeSettings {
    private GraphView graphView;

    public HugradeSettings(){

    }

    public void updateGraphView(GraphView gv) {
        if(this.graphView == gv) return;
        this.graphView = gv;
    }

    private boolean graphValid(){
        boolean valid = graphView!=null && graphView.getGraph()!=null;
        if(!valid) System.err.println("Setting has null graph or graphView");
        return valid;
    }

    private Graph getGraph() {
        return graphView.getGraph();
    }

    public GraphView getGraphView() {
        return graphView;
    }

    public String getNodeMu() {
        if(graphValid()) return getGraph().getNodeMu();
        return "";
    }

    public String getEdgeMu() {
        if(graphValid()) return getGraph().getEdgeMu();
        return "";
    }

    public void setNodeMu(String nodeMu) {
        if(graphValid()){
            getGraph().setNodeMu(nodeMu);
        }
    }

    public void setEdgeMu(String newValue) {
        if(graphValid()){
            getGraph().setEdgeMu(newValue);
        }
    }

    public boolean showNodeValues() {
        if(graphValid()) return getGraph().showNodeValues();
        return false;
    }

    public void setNodeValuesVisible(Boolean newValue) {
        if(graphValid()) getGraph().setNodeValuesVisible(newValue);
    }

    public void setMagnetGrid(boolean enabled){
        if(graphValid()) graphView.setMagnetGridEnabled(enabled);
    }

    public boolean isMagnetGridOn(){
        if(graphValid()) return graphView.isMagnetGridOn();
        return false;
    }

    public boolean areNodeValuesVisible() {
        if(graphValid()){
            return getGraph().areNodeValuesVisible();
        }
        return false;
    }
}
