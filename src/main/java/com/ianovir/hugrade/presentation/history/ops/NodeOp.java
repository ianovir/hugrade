package com.ianovir.hugrade.presentation.history.ops;

import com.ianovir.hugrade.presentation.views.GraphView;
import com.ianovir.hugrade.presentation.views.NodeView;

public class NodeOp implements HistoryOp<GraphView> {
    private OpType type;
    NodeView node;
    double oldX, oldY, newX, newY;
    String oldName, oldDescription, newName, newDescription;

    public NodeOp(NodeView node) {
        this.node = node;
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
            case MOVED:
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
            case MOVED:
                break;
        }
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    public NodeView getNode() {
        return node;
    }

    public void setNode(NodeView node) {
        this.node = node;
    }

    public double getOldX() {
        return oldX;
    }

    public void setOldX(double oldX) {
        this.oldX = oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public void setOldY(double oldY) {
        this.oldY = oldY;
    }

    public double getNewX() {
        return newX;
    }

    public void setNewX(double newX) {
        this.newX = newX;
    }

    public double getNewY() {
        return newY;
    }

    public void setNewY(double newY) {
        this.newY = newY;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }
}
