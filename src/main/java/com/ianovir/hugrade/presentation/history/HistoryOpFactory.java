package com.ianovir.hugrade.presentation.history;

import com.ianovir.hugrade.presentation.history.ops.EdgeOp;
import com.ianovir.hugrade.presentation.history.ops.NodeOp;
import com.ianovir.hugrade.presentation.history.ops.OpType;
import com.ianovir.hugrade.presentation.views.EdgeView;
import com.ianovir.hugrade.presentation.views.NodeView;

public class HistoryOpFactory {
    //TODO: implement HistoryOpFactory

    public static NodeOp createNodeChangedOp(NodeView node,
                                             String oldName, String oldDescr,
                                             String newName, String newDescr){
        NodeOp nop = new NodeOp(node);
        nop.setType(OpType.CHANGED);
        nop.setNewName(newName);
        nop.setNewDescription(newDescr);
        nop.setOldName(oldName);
        nop.setOldDescription(oldDescr);
        return nop;
    }

    public static NodeOp createNodeMovedOp(NodeView node,
                                           double oldX, double oldY,
                                           double newX, double newY){
        NodeOp nop = new NodeOp(node);
        nop.setType(OpType.MOVED);
        nop.setOldX(oldX);
        nop.setOldY(oldY);
        nop.setNewX(newX);
        nop.setNewY(newY);
        return nop;
    }

    public static NodeOp createNodeDeletedOp(NodeView node){
        NodeOp nop = new NodeOp(node);
        nop.setType(OpType.DELETED);
        return nop;
    }

    public static NodeOp createNodeAddedOp(NodeView node){
        NodeOp nop = new NodeOp(node);
        nop.setType(OpType.ADDED);
        return nop;
    }

    public static EdgeOp createEdgeChangedOp(EdgeView edge){
        EdgeOp eop = new EdgeOp(edge);
        eop.setType(OpType.MOVED);
        return eop;
    }

    public static EdgeOp createEdgeMovedOp(){
        return null;
    }

    public static EdgeOp createEdgeDeletedOp(){
        return null;
    }

    public static EdgeOp createEdgeAddedOp(){
        return null;
    }


}
