package com.ianovir.hugrade.core.solvers;

import com.ianovir.hugrade.core.solvers.path.PathSolver;

/**
 * Type wrapping settings for solving bidirectional graphs with negative edges' weights.
 */
public class GraphSolverSettings {

    private PathSolver.BidirectionalConnectionOp mEdgesOp;
    private PathSolver.NegativeEdgesOp negEdgesOp;

    public GraphSolverSettings(PathSolver.BidirectionalConnectionOp mEdgesOp, PathSolver.NegativeEdgesOp negEdgesOp) {
        this.mEdgesOp = mEdgesOp;
        this.negEdgesOp = negEdgesOp;
    }

    public PathSolver.BidirectionalConnectionOp getmEdgesOp() {
        return mEdgesOp;
    }

    public PathSolver.NegativeEdgesOp getNegEdgesOp() {
        return negEdgesOp;
    }
}
