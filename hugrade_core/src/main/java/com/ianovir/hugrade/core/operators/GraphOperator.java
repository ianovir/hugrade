package com.ianovir.hugrade.core.operators;

import com.ianovir.hugrade.core.models.Graph;

public interface GraphOperator{

    /**
     * Executes a job on the input graph
     *
     * @param graph The {@link Graph} to operate in
     */
    void normalize(Graph graph);
}
