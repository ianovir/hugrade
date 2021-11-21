package com.ianovir.hugrade.core.business.operators;

import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

/**
 * Normalizes all the edges of the input {@link Graph}
 */
public class GraphNormalizer implements GraphOperator{

    @Override
    public Graph doWork(Graph graph) {
        for(GNode n: graph.getNodes()){
            graph.normalizeNodeEdges(n);
        }
        return graph;
    }

}
