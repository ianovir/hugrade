package com.ianovir.hugrade.tests.shortestPaths;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

public class TestGraphFactory {

    public static Graph getGraphWithCycles() {
        Graph g = new Graph();
        //source: A(0), end F(5)
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addNodes(new GNode("D", 0, 0));
        g.addNodes(new GNode("E", 0, 0));
        g.addNodes(new GNode("F", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(1f, 1, 2));
        g.addEdges(new GEdge(1f, 2, 3));
        g.addEdges(new GEdge(1f, 3, 5));
        g.addEdges(new GEdge(100f, 0, 4));
        g.addEdges(new GEdge(100f, 4, 5));
        g.addEdges(new GEdge(2f, 1, 4));//cycle
        g.addEdges(new GEdge(2f, 4, 2));//cycle
        g.addEdges(new GEdge(2f, 5, 0));//straight dst->source
        return g;
    }

}
