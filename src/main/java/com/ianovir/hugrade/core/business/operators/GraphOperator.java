package com.ianovir.hugrade.core.business.operators;

import com.ianovir.hugrade.core.models.Graph;

public interface GraphOperator{

    /**
     * Executes a job on the input graph
     * @param graph The {@link Graph} to operate in
     * @return the result of the operation (it can be the same input graph)
     */
    Graph doWork(Graph graph);
}
