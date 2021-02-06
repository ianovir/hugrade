package com.ianovir.hugrade.core.business.lalgebra;

/**
 * Some useful linear algebra operations on matrices
 */
public class MatrixUtils {

    public static float[][] inverse(float[][] m) {
        float[][] inverse = new float[m.length][m.length];

        float det = determinant(m);

        if(m.length==1) return m;
        if(m.length==2) {
            return new float[][]{
                    new float[] {m[1][1]/det, -1*m[0][1]/det},
                    new float[] {-1*m[1][0], m[0][0]/det}
            };
        }
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++)
                //TODO: cast to float
                inverse[i][j] = (float) (Math.pow(-1, i + j)
                        * determinant(getMinor(m, i, j)));

        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j <= i; j++) {
                float temp = inverse[i][j];
                inverse[i][j] = inverse[j][i] / det;
                inverse[j][i] = temp / det;
            }
        }

        return inverse;
    }


    public static float determinant(float[][] matrix) {
        if (matrix.length != matrix[0].length)
            throw new IllegalStateException("invalid dimensions");

        if (matrix.length == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        float det = 0;
        for (int i = 0; i < matrix[0].length; i++)
            det += Math.pow(-1, i) * matrix[0][i]
                    * determinant(getMinor(matrix, 0, i));
        return det;
    }

    public static float[][] getMinor(float[][] m , int r, int c){
        float[][] minor = new float[m.length - 1][m.length - 1];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; i != r && j < m[i].length; j++)
                if (j != c)
                    minor[i < r ? i : i - 1][j < c ? j : j - 1] = m[i][j];
        return minor;
    }

    public static float[][] transpose(float[][] m){
        float[][] ret = new float[m[0].length][m.length];
        for(int r=0;r<ret.length;r++){
            for(int c=0;c<ret[r].length;c++){
                ret[r][c] = m[c][r];
            }
        }
        return ret;
    }

    public static float[][] multiplyMatrices(float[][] a, float[][] b) {
        float[][] ret = new float[a.length][b[0].length];

        for (int r = 0; r < ret.length; r++) {
            for (int c = 0; c < ret[r].length; c++) {
                float sum = 0;
                for (int i = 0; i < b.length; i++) {
                    sum += a[r][i] * b[i][c];
                }
                ret[r][c] = sum;
            }
        }
        return ret;
    }

    public static float[][] subtractMatrices(float[][] a, float[][] b) {
        if(a.length!= b.length) return null;
        float[][] ret = new float[a.length][a.length];
        for(int r=0;r<a.length;r++){
            for(int c=0;c<a.length;c++){
                ret[r][c] = a[r][c] - b[r][c];
            }
        }
        return ret;
    }

    public static float[][] getIdentity(int length) {
        float[][] ret  = new float[length][length];
        for(int rc = 0; rc<length; rc++){
            ret[rc][rc] = 1f;
        }
        return ret;
    }

    public static float[][] normalize(float[][] m) {
        float[][] ret = new float[m.length][m.length];
        for(int r=0;r<m.length;r++){
            float[] row = m[r];
            float sum = 0;
            for(float f : row) sum+=f;
            sum = sum>0? sum:1;
            for(int c=0;c<row.length;c++){
                ret[r][c] = m[r][c]/sum;
            }
        }
        return ret;
    }
}
