package com.ianovir.hugrade.core.business.solvers.path;

import com.ianovir.hugrade.core.business.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

import java.util.*;

/**
 * Implementation of the A-star shortest-path algorithm for graphs
 */
public class AStarSolver extends PathSolver{

    public AStarSolver(Graph graph){
        super(graph);
    }

    @Override
    public int[] solve(int source, int target) {
        return solve(source, target, new GraphSolverSettings(BidirectionalConnectionOp.LIGHTEST, NegativeEdgesOp.ZERO) );
    }

    @Override
    public int[] solve(int source, int target, GraphSolverSettings settings) {
        costs = null;
        ArrayList<Integer> openSet = new ArrayList<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();

        openSet.add(source);

        float[] gScore = initGScore(source);
        float[] fScore = initFScore(source);

        while(!openSet.isEmpty()){
            //the node in openSet having the lowest fScore[] value
            Integer current = openSet.stream()
                    .reduce((i,j) -> fScore[i] > fScore[j] ? j : i)
                    .get();

            if(current == target){
                ArrayList<Integer> path = reconstructPath(cameFrom, current);
                path.add(0, source);
                computeCosts(path);
                return path.stream().mapToInt(i->i).toArray();
            }

            openSet.remove(current);
            for(GNode neighbor: graph.getNeighbors(current)){

                GEdge currentEdge = handleNeighborEdge(current, neighbor, settings);
                if(currentEdge==null) continue;

                float tentativeGScore = gScore[current] + currentEdge.getWeight();

                int neighborId = graph.getNodeId(neighbor);

                if(tentativeGScore< gScore[neighborId]){
                    cameFrom.put(neighborId,current);
                    gScore[neighborId] = tentativeGScore;
                    fScore[neighborId] = gScore[neighborId] + heuristicFunction(neighborId);
                    if(openSet.size()==0 || !openSet.contains(neighborId)){
                        openSet.add(neighborId);
                    }
                }
            }
        }
        return null;
    }

    private float[] initFScore(int source) {
        float[] fScore = new float[graph.getNodes().size()];
        Arrays.fill(fScore, Float.MAX_VALUE);
        fScore[source] = heuristicFunction(source);
        return fScore;
    }

    private float[] initGScore(int source) {
        float[] gScore = new float[graph.getNodes().size()];
        Arrays.fill(gScore, Float.MAX_VALUE);
        gScore[source] = 0f;
        return gScore;
    }

    public ArrayList<Integer> reconstructPath(Map<Integer, Integer> cameFrom, int current){
        if(cameFrom.get(current)!=null){
            ArrayList<Integer> p = reconstructPath(cameFrom, cameFrom.get(current));
            p.add(current);
            return p;
        }else{
            return new ArrayList<>();
        }
    }

    private float heuristicFunction(int h){
        //TODO: improve heuristic function
        return 0;
    }


}