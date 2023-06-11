package com.ianovir.hugrade.core.solvers.path;

import com.ianovir.hugrade.core.converters.Graph2TransMatrixConverter;
import com.ianovir.hugrade.core.solvers.GraphSolverSettings;
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
        //initialize
        float[][] tm = Graph2TransMatrixConverter.convert(graph);
        float[] distance = initDistances(tm, source);
        int[] predecessor = initPredecessors(tm);

        handleEdges(tm, settings);
        relaxEdges(tm, distance, predecessor);

        boolean negativeCycleDetected =  checkNegativeCycle(tm, distance);
        if(negativeCycleDetected) return null;

        ArrayList<Integer> path = reconstructPath(source, target, tm, predecessor);
        computeCosts(path);

        return path.stream().mapToInt(i->i).toArray();

    }

    private float[] initDistances(float[][] tm, int source) {
        float[] distance = new float[tm.length];
        Arrays.fill(distance, Float.MAX_VALUE);
        distance[source]=0;
        return distance;
    }

    private int[] initPredecessors(float[][] tm) {
        int[] predecessor = new int[tm.length];
        Arrays.fill(predecessor, -1);
        return predecessor;
    }

    private ArrayList<Integer> reconstructPath(int source, int target, float[][] tm, int[] predecessor) {
        ArrayList<Integer> path = new ArrayList<>();
        path.add(target);
        int currentNode = target;
        while(currentNode!= source && currentNode!=-1){
            path.add(0, predecessor[currentNode]);
            if(predecessor[currentNode]!=-1){
                float w = tm[predecessor[currentNode]][currentNode];
            }
            currentNode = predecessor[currentNode];
        }
        return path;
    }

    private void relaxEdges(float[][] tm, float[] distance, int[] predecessor) {
        for(int relax = 0; relax< tm.length; relax++){
            for(int u = 0; u< tm.length; u++){
                for(int v = 0; v< tm.length; v++){
                    if(tm[u][v]!=0 && distance[u] + tm[u][v] < distance[v]){
                        distance[v] = distance[u] + tm[u][v];
                        predecessor[v] = u;
                    }
                }
            }
        }
    }

    private boolean checkNegativeCycle(float[][] tm, float[] distance) {
        for(int u = 0; u< tm.length; u++){
            for(int v = 0; v< tm.length; v++){
                if(tm[u][v]!=0 && distance[u] + tm[u][v] < distance[v]){
                    System.err.println("Negative-weight cycle detected");
                    return true;
                }
            }
        }
        return false;
    }

}
