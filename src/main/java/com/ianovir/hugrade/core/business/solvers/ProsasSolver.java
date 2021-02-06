package com.ianovir.hugrade.core.business.solvers;

import com.ianovir.hugrade.core.business.lalgebra.MatrixUtils;
import com.ianovir.hugrade.core.business.converters.Graph2TransMatrixConverter;
import com.ianovir.hugrade.core.models.Graph;

import java.util.ArrayList;

/**
 * Probability solver of absorbing states in Markov chain.
 */
public class ProsasSolver {

    /**
     * Solves the probability of absorbing states. The node with ID=0 is considered the starting one.
     * @param graph the {@link Graph} to be solved, interpreted as a Markov chain
     * @return a nx2 matrix F, where n is the number of the absorbing states. the
     */
    public static float[][] solve(Graph graph){
        try{

            graph.sortNodes();

            System.out.println("Computing transient matrix...");
            float[][] m = Graph2TransMatrixConverter.convert(graph);
            if(m.length==2 || m.length==1) {
                return new float[][]{
                        new float[]{1},
                        new float[]{1}
                };
            }

            System.out.println("Normalizing transient matrix...");
            m =  MatrixUtils.normalize(m);

            System.out.println("Looking for transient and absorbing states...");
            ArrayList<Integer> transientStates = getTransientStates(m);
            ArrayList<Integer> absorbingStates = getAbsorbingStates(m);

            if(absorbingStates.size()<=0){
                System.out.println("No absorbing states");
                return null;
            }

            float[][] matrixQ = getQ(m, transientStates);
            float[][] matrixR = getR(m, transientStates, absorbingStates);
            float[][] identityMatrix = MatrixUtils.getIdentity(matrixQ.length);
            float[][] matrixIminusQ = MatrixUtils.subtractMatrices(identityMatrix, matrixQ);

            float[][] N =  MatrixUtils.inverse(matrixIminusQ);
            float[][] F =  MatrixUtils.multiplyMatrices(N, matrixR);

            float[][] ret = new float[F[0].length][2];
            for(int i =0;i<F[0].length;i++){
                ret[i][0] = absorbingStates.get(i);
                ret[i][1] = F[0][i];
            }

            return ret;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }


    private static float[][] getR(float[][] m, ArrayList<Integer> t, ArrayList<Integer> a) {
        float[][] ret = new float[t.size()][a.size()];
        for(int r=0;r<t.size();r++){
            for(int c=0;c<a.size();c++){
                ret[r][c] = m[t.get(r)][a.get(c)];
            }
        }
        return ret;
    }

    private static float[][] getQ(float[][] m, ArrayList<Integer> t) {
        float[][] ret = new float[t.size()][t.size()];
        for(int r=0;r<t.size();r++){
            for(int c=0;c<t.size();c++){
                ret[r][c] = m[t.get(r)][t.get(c)];
            }
        }
        return ret;
    }

    private static ArrayList<Integer> getAbsorbingStates(float[][] m) {
        ArrayList<Integer> t = new ArrayList<>();
        for(int i = 0 ;i<m.length;i++){
            float[] r = m[i];
            float sum = 0;
            for(float f : r) sum+=f;
            if(sum ==0) t.add(i);
        }
        return t;
    }

    private static ArrayList<Integer> getTransientStates(float[][] m) {
        ArrayList<Integer> t = new ArrayList<>();
        for(int i = 0 ;i<m.length;i++){
            float[] r = m[i];
            float sum = 0;
            for(float f : r) sum+=f;
            if(sum !=0) t.add(i);
        }
        return t;
    }

}
