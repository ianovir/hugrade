package com.ianovir.hugrade.managers.history;

import com.ianovir.hugrade.managers.history.ops.HistoryOp;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.GraphView;

import java.util.Stack;

public class HistoryManager {
    //TODO: implement HistoryManager

    private final Graph graph;
    private int SIZE = 20;
    Stack<HistoryOp<GraphView>> historyStack;


    public HistoryManager(int size, Graph g){
        this.graph = g;
        historyStack = new Stack<>();
    }


    public void undo(){

    }

    public void redo(){

    }



}
