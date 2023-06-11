import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.core.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.solvers.ProsasSolver;
import com.ianovir.hugrade.core.solvers.path.DijkstraSolver;
import com.ianovir.hugrade.core.solvers.path.PathSolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestProsasMarkov {

    @Test
    public void testProsas_reachableNodes() {
        Graph g = new Graph();
        g.addNodes(new GNode("s0"));
        g.addNodes(new GNode("s1"));
        g.addNodes(new GNode("s2"));
        g.addNodes(new GNode("s3"));
        g.addNodes(new GNode("s4"));
        g.addEdges(new GEdge(2f/3f, 0, 1 ));
        g.addEdges(new GEdge(1f/3f, 0, 2));
        g.addEdges(new GEdge(3f/7f, 1, 3));
        g.addEdges(new GEdge(4f/7f, 1, 4));

        float[][] result = ProsasSolver.solve(g);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(2, result[0][0]);
        assertEquals(7/21f, result[0][1], 1E-7);
        assertEquals(3, result[1][0]);
        assertEquals(6/21f, result[1][1], 1E-7);
        assertEquals(4, result[2][0]);
        assertEquals(8/21f, result[2][1], 1E-7);
    }

    @Test
    public void testProsas_oneNonReachableNode() {
        Graph g = new Graph();
        g.addNodes(new GNode("s0"));
        g.addNodes(new GNode("s1"));
        g.addNodes(new GNode("s2"));
        g.addNodes(new GNode("s3"));
        g.addNodes(new GNode("s4"));
        g.addNodes(new GNode("s5"));
        g.addEdges(new GEdge(1f/2f, 0, 1 ));
        g.addEdges(new GEdge(1f/2f, 0, 5 ));
        g.addEdges(new GEdge(3f/9f, 1, 3 ));
        g.addEdges(new GEdge(2f/9f, 1, 4 ));
        g.addEdges(new GEdge(4f/9f, 1, 0 ));

        float[][] result = ProsasSolver.solve(g);

        assertNotNull(result);
        assertEquals(4, result.length);
        assertEquals(2, result[0][0]);
        assertEquals(0f, result[0][1], 1E-7);
        assertEquals(3, result[1][0]);
        assertEquals(3/14f, result[1][1], 1E-7);
        assertEquals(4, result[2][0]);
        assertEquals(2/14f, result[2][1], 1E-7);
        assertEquals(5, result[3][0]);
        assertEquals(9/14f, result[3][1], 1E-7);
    }

    @Test
    public void testProsas_negativeWeight_simple() {
        Graph g = new Graph();
        g.addNodes(new GNode("s0"));
        g.addNodes(new GNode("s1"));
        g.addNodes(new GNode("s2"));
        g.addEdges(new GEdge(1f/2f, 0, 1 ));
        g.addEdges(new GEdge(-1f/2f, 0, 2 ));

        float[][] result = ProsasSolver.solve(g);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(1, result[0][0]);
        assertEquals(0.5f, result[0][1], 1E-7);
        assertEquals(2, result[1][0]);
        assertEquals(0.5f, result[1][1], 1E-7);
    }

    @Test
    public void testProsas_negativeWeight() {
        Graph g = new Graph();
        g.addNodes(new GNode("s0"));
        g.addNodes(new GNode("s1"));
        g.addNodes(new GNode("s2"));
        g.addNodes(new GNode("s3"));
        g.addNodes(new GNode("s4"));
        g.addEdges(new GEdge(2f/3f, 0, 1 ));
        g.addEdges(new GEdge(-1f/3f, 0, 2));
        g.addEdges(new GEdge(-3f/7f, 1, 3));
        g.addEdges(new GEdge(-4f/7f, 1, 4));

        float[][] result = ProsasSolver.solve(g);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(2, result[0][0]);
        assertEquals(7/21f, result[0][1], 1E-7);
        assertEquals(3, result[1][0]);
        assertEquals(6/21f, result[1][1], 1E-7);
        assertEquals(4, result[2][0]);
        assertEquals(8/21f, result[2][1], 1E-7);
    }






}
