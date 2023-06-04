import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.core.operators.GraphNormalizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGraphNormalizer {

    @Test
    public void testNormalizeSimpleGraph() {
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(1f, 0, 2));

        var gNormalizer = new GraphNormalizer();
        gNormalizer.normalize(g);

        assertEquals(0.5, g.getEdges().get(0).getWeight());
        assertEquals(0.5, g.getEdges().get(1).getWeight());
    }

    @Test
    public void testNormalizeCompositeGraph() {
        Graph g = new Graph();
        g.addNodes(new GNode("A", 0, 0));
        g.addNodes(new GNode("B", 0, 0));
        g.addNodes(new GNode("C", 0, 0));
        g.addNodes(new GNode("C1", 0, 0));
        g.addNodes(new GNode("C2", 0, 0));
        g.addEdges(new GEdge(1f, 0, 1));
        g.addEdges(new GEdge(1f, 0, 2));
        g.addEdges(new GEdge(4f, 2, 3));
        g.addEdges(new GEdge(6f, 2, 4));

        var gNormalizer = new GraphNormalizer();
        gNormalizer.normalize(g);

        assertEquals(0.5, g.getEdges().get(0).getWeight());
        assertEquals(0.5, g.getEdges().get(1).getWeight());
        assertEquals(0.4f, g.getEdges().get(2).getWeight());
        assertEquals(0.6f, g.getEdges().get(3).getWeight());
    }

}
