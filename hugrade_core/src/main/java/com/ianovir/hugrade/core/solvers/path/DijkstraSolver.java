package com.ianovir.hugrade.core.solvers.path;
import com.ianovir.hugrade.core.converters.Graph2TransMatrixConverter;
import com.ianovir.hugrade.core.solvers.GraphSolverSettings;
import com.ianovir.hugrade.core.models.Graph;

import java.util.*;

/**
 * Implementation of the Dijkstra shortest-path algorithm for graphs
 */
public class DijkstraSolver extends PathSolver {

    public DijkstraSolver(Graph graph) {
        super(graph);
    }

    @Override
    public int[] solve(int source, int target) {
        return solve(source, target, new GraphSolverSettings(BidirectionalConnectionOp.LIGHTEST, NegativeEdgesOp.ZERO) );
    }

    @Override
    public int[] solve(int source, int target, GraphSolverSettings settings) {
        float[][] tm = Graph2TransMatrixConverter.convert(graph);
        handleEdges(tm, settings);

        float distance = 0;
        int V = tm[0].length;

        List<PrioritizedVert> vertSet = new ArrayList<>();
        Comparator<? super PrioritizedVert> comp = Comparator.comparingDouble(o -> o.priority);

        //init some stuff:
        float[] dist = new float[V];
        int[] prev = new int[V];
        dist[source] = 0;

        for(int v = 0; v<tm[0].length; v++){
            if(v!=source){
                dist[v] = Integer.MAX_VALUE;
                prev[v] = -1;
            }
            vertSet.add(new PrioritizedVert(v, dist[v]));
        }

        //sorting list by distances:
        vertSet.sort(comp);

        while(!vertSet.isEmpty()){
            PrioritizedVert u = vertSet.remove(0);

            //breaking here to enjoy the path:
            if(u.v == target)
                break;

            //working on neighbors of u:
            for(int n = 0 ; n< V; n++){
                if(n==source || tm[u.v][n]<=0) continue;
                float alt = dist[u.v] + tm[u.v][n];
                if(alt < dist[n]){

                    dist[n]  = alt;
                    prev[n] = u.v;

                    for(PrioritizedVert pv : vertSet){
                        if(pv.v==n){
                            pv.priority = alt;
                            vertSet.sort(comp);
                            break;
                        }
                    }

                }
            }
        }

        ArrayList<Integer> path = new ArrayList<>();
        int uu = target;
        if(prev[uu]>=0 || uu == source){
            while(uu != source ){
                path.add(uu);
                float dd = tm [prev[uu]][uu];
                uu = prev[uu];
                distance += dd;
            }
        }

        Collections.reverse(path);
        path.add(0, source);

        System.out.println(String.format("Path from %d to %d", source, target));
        for(int s: path) System.out.print(s + ", ");
        System.out.println(String.format("Distance: %f", distance));

        //update costs
        computeCosts(path);

        return path.stream().mapToInt(i->i).toArray();
    }

    static class PrioritizedVert{
        int v;
        float priority;
        PrioritizedVert(int v, float priority){
            this.v = v;
            this.priority = priority;
        }
    }

}
