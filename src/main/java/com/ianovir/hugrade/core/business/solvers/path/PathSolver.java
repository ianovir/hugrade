package com.ianovir.hugrade.core.business.solvers.path;

import com.ianovir.hugrade.core.business.solvers.GraphSolver;
import com.ianovir.hugrade.core.business.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

import java.util.ArrayList;

/**
 * Graph solver concerning path problems.
 */
public abstract class PathSolver implements GraphSolver {
    /**
     * Defines how to solve bidirectional connections
     */
    public enum BidirectionalConnectionOp {
        /**
         * The edge with the lightest weight is chosen
         */
        LIGHTEST,
        /**
         * The edge with the heaviest weight is chosen
         */
        HEAVIEST,
        /**
         * The edge connecting specific source and destination node is chosen
         */
        DIRECT;
    }

    /**
     * Specifies how to manage edges with negative weight
     */
    public enum NegativeEdgesOp{
        /**
         * weight is considered as absolute value
         */
        ABSOLUTE,
        /**
         *  weight is considered as 0 (ignore weight)
         */
        ZERO,
        /**
         * Do not change negative weights (for algorithms supporting negative edges)
         */
        AS_IS;
    }

    protected final Graph graph;
    protected float[][] costs;

    public PathSolver(Graph graph){
        this.graph = graph;
    }

    /**
     * Compute path algorithm on a graph
     * @param source the source node's index
     * @param target the target node's index
     * @return A list of all nodes belonging to the path.
     */
    public abstract int[] solve(int source, int target);

    /**
     * Compute path algorithm on a graph
     * @param source the source node's index
     * @param target the target node's index
     * @param settings settings to manage graph configurations
     * @return A list of all nodes belonging to the path.
     */
    public abstract int[] solve(int source, int target, GraphSolverSettings settings);

    public static void handleEdges(float[][] tm, GraphSolverSettings settings) {
        for(int r=0;r<tm.length;r++){

            //eliminating edges n->n
            if(tm[r][r]!=0) tm[r][r]=0;

            for(int c=0;c<tm[r].length;c++){
                //negative
                if(tm[r][c]<0){
                    switch (settings.getNegEdgesOp()){
                        case ZERO:
                            tm[r][c]= 0;
                            break;
                        case ABSOLUTE:
                            tm[r][c]= Math.abs(tm[r][c]= 0);
                            break;
                        case AS_IS:
                            break;
                    }
                }

                //bidirectional
                if((tm[r][c]!=0 || tm[c][r]!=0) && settings.getmEdgesOp()== BidirectionalConnectionOp.LIGHTEST){
                    float min = Math.min(tm[r][c], tm[c][r]);
                    if(min!=0){
                        tm[r][c] = tm[c][r] = min;
                    }else{
                        tm[r][c] = tm[c][r] = Math.max(tm[r][c], tm[c][r]);
                    }
                }
                if(settings.getmEdgesOp()== BidirectionalConnectionOp.DIRECT){
                    tm[c][r] = 0f;
                }

            }
        }
    }

    protected GEdge handleNeighborEdge(Integer current, GNode neighbor, GraphSolverSettings settings) {
        //ignore edges with same dst and target
        if(current==neighbor.getId()) return null;
        GEdge currentEdge  = graph.getEdgesByNodeIDs(current, neighbor.getId());
        GEdge bidirectionalEdge  = graph.getEdgesByNodeIDs(neighbor.getId(), current);

        if (currentEdge == null) {
            if (settings.getmEdgesOp() == BidirectionalConnectionOp.DIRECT) {
                return null;//only direct edges are admitted
            } else {
                currentEdge = bidirectionalEdge;
            }
        }
        if(currentEdge==null) return null;

        //negative weight
        if(currentEdge.getWeight()<0){
            switch (settings.getNegEdgesOp()){
                case ABSOLUTE:
                    currentEdge.setWeight(Math.abs(currentEdge.getWeight()));
                    break;
                case ZERO:
                    return null;
            }
        }

        //bidirectional
        if(settings.getmEdgesOp().equals(BidirectionalConnectionOp.LIGHTEST) && bidirectionalEdge!=null){
            if(currentEdge.getWeight()>=bidirectionalEdge.getWeight()) currentEdge = bidirectionalEdge;
        }

        return currentEdge;
    }

    protected void computeCosts(ArrayList<Integer> path) {
        //TODO: fix this
        this.costs = new float[path.size()][2];
        float sum = 0;
        for(int i = 1;i<costs.length;i++){
            GEdge e = graph.getEdgesByNodeIDs(path.get(i - 1), path.get(i));
            if(e==null) e = graph.getEdgesByNodeIDs(path.get(i), path.get(i-1));
            sum += e.getWeight();
            costs[i][0] = e.getWeight();
            costs[i][1] = sum;
        }
    }

    /**
     * Retrieves the costs, node by node of the solved path
     * @return A nx2 matrix where the element (i,0) represent the cost for reaching the node i from i-1, and the element
     * (i,1) represent the overall cost for reaching node i from the starting one. Can return null.
     */
    public float[][] getCosts() {
        return costs;
    }

}
