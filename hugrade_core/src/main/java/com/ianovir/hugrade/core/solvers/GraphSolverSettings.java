package com.ianovir.hugrade.core.solvers;

import com.ianovir.hugrade.core.solvers.path.PathSolver;

public record GraphSolverSettings(PathSolver.BidirectionalConnectionOp mEdgesOp,
                                  PathSolver.NegativeEdgesOp negEdgesOp) {

}
