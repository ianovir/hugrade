package com.ianovir.hugrade.core.converters;

import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

import java.util.List;

/**
 * Graph to raw transition matrix operator.
 */
public class Graph2NodesMatrixConverter {

    /**
     * Converts a to a nodes matrix
     * @param graph the input {@link Graph}
     * @return a matrix M nx3, M[i] represents the i-th node of the graph with:
     * M[i][0] is the node's id
     * M[i][1] is the node's name
     * M[i][2] is the node's value
     */
    static public String[][] convert(Graph graph) {
        if(graph==null) return null;
        List<GNode> nodes = graph.getNodes();
        int non = nodes.size();

        String[][] ret = new String[non][3];

        for(int i = 0 ;i<nodes.size();i++){
            int nodeId = graph.getNodeId(nodes.get(i));
            ret[i][0]= nodeId + "";
            ret[i][1]= nodes.get(i).getName();
            ret[i][2]= String.format("%.2f", nodes.get(i).getValue()) ;
        }

        return ret;
    }

}
