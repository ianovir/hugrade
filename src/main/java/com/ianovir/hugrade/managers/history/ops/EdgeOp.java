package com.ianovir.hugrade.managers.history.ops;

import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.GraphView;

public class EdgeOp implements  HistoryOp<GraphView> {
    OpType type;
    EdgeView edge;

    public EdgeOp(EdgeView edge) {
        this.edge = edge;
    }

    @Override
    public void onDo(GraphView graph) {
        switch (type){
            case ADDED:
                break;
            case CHANGED:
                break;
            case DELETED:
                break;
        }
    }

    @Override
    public void onUndo(GraphView graph) {
        switch (type){
            case ADDED:
                break;
            case CHANGED:
                break;
            case DELETED:
                break;
        }
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }
}
