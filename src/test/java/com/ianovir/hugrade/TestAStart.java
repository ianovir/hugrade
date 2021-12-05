package com.ianovir.hugrade;

import com.ianovir.hugrade.core.business.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.business.solvers.path.AStarSolver;
import com.ianovir.hugrade.core.business.solvers.path.PathSolver;
import com.ianovir.hugrade.core.models.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAStart {

    @Test
    public void testShortestPath_CycleGraph_direct() {
        Graph g = TestGraphFactory.getGraphWithCycles();
        PathSolver solver = new AStarSolver(g);
        GraphSolverSettings settings = new GraphSolverSettings(
                PathSolver.BidirectionalConnectionOp.DIRECT,
                PathSolver.NegativeEdgesOp.ZERO);

        int[] result = solver.solve(0,5, settings);

        assertEquals(5, result.length);
        assertEquals(0, result[0]);//src
        assertEquals(1, result[1]);
        assertEquals(2, result[2]);
        assertEquals(3, result[3]);
        assertEquals(5, result[4]);//dst
    }

    @Test
    public void testShortestPath_CycleGraph_lightest() {
        Graph g =  TestGraphFactory.getGraphWithCycles();
        PathSolver solver = new AStarSolver(g);
        GraphSolverSettings settings = new GraphSolverSettings(
                PathSolver.BidirectionalConnectionOp.LIGHTEST,
                PathSolver.NegativeEdgesOp.ZERO);

        int[] result = solver.solve(0,5, settings);

        assertEquals(2, result.length);
        assertEquals(0, result[0]);//src
        assertEquals(5, result[1]);//dst
    }

}
