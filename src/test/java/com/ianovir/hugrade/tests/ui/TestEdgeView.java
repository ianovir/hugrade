package com.ianovir.hugrade.tests.ui;

import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.NodeView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestEdgeView {

    @Test
    public void testPointOnPath_withTolerance() {
        Graph g = new Graph();
        NodeView start = new NodeView("", 0,0,1);
        NodeView end = new NodeView("", 10,10,1);
        EdgeView ev = new EdgeView(g, start, end, 1 );
        double poX = 1.875;
        double poY = 5.0;

        boolean hit = ev.intersects(poX, poY, 1,1);

        assertTrue(hit);
    }

    @Test
    public void testPointOutOfPath() {
        Graph g = new Graph();
        NodeView start = new NodeView("", 0,0,1);
        NodeView end = new NodeView("", 10,10,1);
        EdgeView ev = new EdgeView(g, start, end, 1 );
        double poX = 1.876;
        double poY = 7.0;

        boolean hit = ev.intersects(poX, poY, 0,0);

        assertFalse(hit);
    }

}
