package com.ianovir.hugrade.tests;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCoreGraph {


    @Test
    public void testCorrectNodeIds() {
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(1f, 0, 2));

        assertEquals(0, g.getEdges().get(0).getSource());
        assertEquals(1, g.getEdges().get(0).getDestination());
        assertEquals(0, g.getEdges().get(1).getSource());
        assertEquals(2, g.getEdges().get(1).getDestination());
    }

    @Test
    public void testSetZeroNode(){
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(1f, 0, 2));
        g.addEdges(new GEdge(1f, 2, 2)); //C->C

        g.changeNodeID(2 ,0);

        assertEquals(2, g.getEdges().get(0).getSource());
        assertEquals(1, g.getEdges().get(0).getDestination());
        assertEquals(2, g.getEdges().get(1).getSource());
        assertEquals(0, g.getEdges().get(1).getDestination());
        assertEquals(0, g.getEdges().get(2).getDestination());
        assertEquals(0, g.getEdges().get(2).getDestination());
    }

    @Test
    public void testNormalizeNodeEdges_equallyWeightedEdges(){
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addEdges(new GEdge(4f, 0, 1));
        g.addEdges(new GEdge(4f, 0, 2));

        g.normalizeNodeEdges(g.getNodeById(0));

        assertEquals(0.5, g.getEdges().get(0).getWeight());
        assertEquals(0.5, g.getEdges().get(1).getWeight());
    }

    @Test
    public void testNormalizeNodeEdges_differentlyWeightedEdges(){
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(3f, 0, 0)); //a->a

        g.normalizeNodeEdges(g.getNodeById(0));

        assertEquals(0.25, g.getEdges().get(0).getWeight());
        assertEquals(0.75, g.getEdges().get(1).getWeight());
    }

    @Test
    public void testSwapEdgeNodes_differentNodes(){
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));

        g.getEdges().get(0).swapNodesIds(0,1);

        assertEquals(1, g.getEdges().get(0).getSource());
        assertEquals(0, g.getEdges().get(0).getDestination());
    }


}
