import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.fx.views.EdgeView;
import com.ianovir.hugrade.fx.views.NodeView;
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

        boolean hit = ev.contains(poX, poY);

        assertTrue(hit);
    }

    @Test
    public void testPointOutOfPath() {
        Graph g = new Graph();
        NodeView start = new NodeView("", 0,0,1);
        NodeView end = new NodeView("", 10,10,1);
        EdgeView ev = new EdgeView(g, start, end, 1 );
        double poX = 2;
        double poY = 7.0;

        boolean hit = ev.contains(poX, poY);

        assertFalse(hit);
    }

    @Test
    public void testSwapExtremes() {
        Graph g = new Graph();
        NodeView start = new NodeView("Origin", 0,0,1);
        NodeView end = new NodeView("Destination", 10,10,1);
        EdgeView ev = new EdgeView(g, start, end, 1 );

        assertEquals(start, ev.getOrigin());
        assertEquals(end, ev.getDestination());

        ev.swapExtremes();

        assertEquals(end, ev.getOrigin());
        assertEquals(start, ev.getDestination());
    }

}
