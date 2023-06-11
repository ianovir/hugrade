package com.ianovir.hugrade.core.operators;

import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

/**
 * Performs a stochastic normalization all the edges of the input {@link Graph}
 */
public class GraphNormalizer implements GraphOperator{

    @Override
    public void normalize(Graph graph) {
        for(GNode n: graph.getNodes()){
            graph.stochasticNormalizeNodeEdges(n);
        }
    }

}
