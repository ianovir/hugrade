package com.ianovir.hugrade.core.business.solvers.path;

import com.ianovir.hugrade.core.business.converters.Graph2TransMatrixConverter;
import com.ianovir.hugrade.core.business.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.models.Graph;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of the Bellman-Ford shortest-path algorithm for graphs
 */
public class BellmanFordSolver extends PathSolver{

    public BellmanFordSolver(Graph graph) {
        super(graph);
    }

    @Override
    public int[] solve(int source, int target) {
        return solve(source, target, new GraphSolverSettings(BidirectionalConnectionOp.LIGHTEST,
                                                                NegativeEdgesOp.AS_IS));
    }

    @Override
    public int[] solve(int source, int target, GraphSolverSettings settings) {

        graph.sortNodes();

        //initialize
        float[][] tm = Graph2TransMatrixConverter.convert(graph);
        handleEdges(tm, settings);

        float[] distance = new float[tm.length];
        Arrays.fill(distance, Float.MAX_VALUE);
        int[] predecessor = new int[tm.length];
        Arrays.fill(predecessor, -1);

        distance[source]=0;

        //relax edges
        for(int relax=0;relax<tm.length;relax++){
            for(int u=0;u<tm.length;u++){
                for(int v=0;v<tm.length;v++){
                    if(tm[u][v]!=0 && distance[u] + tm[u][v] < distance[v]){
                        distance[v] = distance[u] + tm[u][v];
                        predecessor[v] = u;
                    }
                }
            }
        }

        //check for negative-weight cycles
        for(int u=0;u<tm.length;u++){
            for(int v=0;v<tm.length;v++){
                if(tm[u][v]!=0 && distance[u] + tm[u][v] < distance[v]){
                    System.err.println("Negative-weight cycle detected");
                    return null;
                }
            }
        }

        //reconstruct path
        ArrayList<Integer> path = new ArrayList<>();
        path.add(target);
        int currentNode = target;
        while(currentNode!=source && currentNode!=-1){
            path.add(0,predecessor[currentNode]);
            if(predecessor[currentNode]!=-1){
                float w = tm[predecessor[currentNode]][currentNode];
            }
            currentNode = predecessor[currentNode];
        }

        computeCosts(path);
        return path.stream().mapToInt(i->i).toArray();

    }

}
