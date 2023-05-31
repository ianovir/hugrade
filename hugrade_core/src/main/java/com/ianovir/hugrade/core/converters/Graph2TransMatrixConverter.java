package com.ianovir.hugrade.core.converters;


import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.Graph;

import java.util.List;

/**
 * Graph to raw transition matrix operator.
 */
public class Graph2TransMatrixConverter {

    /**
     * Converts the input graph to a transition matrix
     * @param graph the input {@link Graph}
     * @return A matrix E nxn, where  E[i][j] is the weight of the edge connecting
     * the node i to the node j. {{@code null}} if the input is null.
     */
    static public float[][] convert(Graph graph) {
        if(graph==null) return null;

        List<GEdge> edges = graph.getEdges();
        int non = graph.getNodes().size();

        float[][] ret = new float[non][non];

        for(GEdge e : edges){
            ret[e.getSource()][e.getDestination()] = e.getWeight();
        }

        return ret;
    }

}
