import com.ianovir.hugrade.core.lalgebra.MatrixUtils;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

public class TestMatrixUtils {

    @Test
    public void testInverseMatrixWithZeroDeterminant_throwException() {
        var m = new float[][]{
                {1f, 2f, 3f},
                {1f, 2f, 3f},
                {1f, 2f, 3f}};

        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,
                () -> MatrixUtils.inverse(m),
                "Expected inverse to throw, but it didn't"
        );
        assertEquals("Input matrix has zero determinant", thrown.getMessage());
    }

    @Test
    public void testInverseMatrixR1() {
        var m = new float[][]{{9}};

        var inverse = MatrixUtils.inverse(m);

        assertEquals(9f, inverse[0][0]);

    }
    @Test
    public void testInverseMatrixR2() {
        var m = new float[][]{
                {4f, 7f},
                {2f, 6f}};

        var inverse = MatrixUtils.inverse(m);

        assertEquals(0.6f, inverse[0][0]);
        assertEquals(-0.7f, inverse[0][1]);

        assertEquals(-0.2f, inverse[1][0]);
        assertEquals(0.4f, inverse[1][1]);

    }

    @Test
    public void testInverseMatrixR3() {
        var m = new float[][]{
                {1f, 2f, -1f},
                {2f, 1f, 2f},
                {-1f, 2f, 1f}};

        var inverse = MatrixUtils.inverse(m);

        assertEquals(0.1875f, inverse[0][0]);
        assertEquals(0.25f, inverse[0][1]);
        assertEquals(-0.3125f, inverse[0][2]);

        assertEquals(0.25f, inverse[1][0]);
        assertEquals(0f, inverse[1][1], 0.0001f);
        assertEquals(0.25f, inverse[1][2]);

        assertEquals(-0.3125f, inverse[2][0]);
        assertEquals(0.25f, inverse[2][1]);
        assertEquals(0.1875f, inverse[2][2]);
    }

    @Test
    public void testIdentityMatrixR1() {
        var identity = MatrixUtils.getIdentity(1);
        assertEquals(1, identity[0][0]);
    }

    @Test
    public void testIdentityMatrixR3() {
        var identity = MatrixUtils.getIdentity(3);

        assertEquals(1, identity[0][0]);
        assertEquals(0, identity[0][1]);
        assertEquals(0, identity[0][2]);

        assertEquals(0, identity[1][0]);
        assertEquals(1, identity[1][1]);
        assertEquals(0, identity[1][2]);

        assertEquals(0, identity[2][0]);
        assertEquals(0, identity[2][1]);
        assertEquals(1, identity[2][2]);
    }

    @Test
    public void testStochasticNormalizeMatrixR3() {
        var m = new float[][]{
                {10f, 20f, -70f},
                {2f, 8f, 0f},
                {-25f, 50f, 25f}};

        var normalized = MatrixUtils.stochasticNormalize(m);

        assertEquals(0.1f, normalized[0][0]);
        assertEquals(0.2f, normalized[0][1]);
        assertEquals(0.7f, normalized[0][2]);

        assertEquals(0.2f, normalized[1][0]);
        assertEquals(0.8f, normalized[1][1], 0.0001f);
        assertEquals(0.0f, normalized[1][2]);

        assertEquals(0.25f, normalized[2][0]);
        assertEquals(0.5f, normalized[2][1]);
        assertEquals(0.25f, normalized[2][2]);
    }


    @Test
    public void testSubtractMatrices_invalidSize() {
        var a = new float[][]{
                {1, 2f, 3f},
                {-5f, -6f, -7f},
                {-12f, -9f, 3f}};
        var b = new float[][]{
                {-4, -5f, 2f},
                {0.5f, 24f, 5f}};

        var sub = MatrixUtils.subtractMatrices(a, b);

        assertNull(sub);
    }

    @Test
    public void testSubtractMatricesR1() {
        var a = new float[][]{{11f}};
        var b = new float[][]{{5f}};

        var sub = MatrixUtils.subtractMatrices(a, b);

        assertNotNull(sub);
        assertEquals(6f, sub[0][0]);
    }

    @Test
    public void testSubtractMatricesR3() {
        var a = new float[][]{
                {1, 2f, 3f},
                {-5f, -6f, -7f},
                {-12f, -9f, 3f}};
        var b = new float[][]{
                {-4, -5f, 2f},
                {0.5f, 24f, 5f},
                {-12f, 0.25f, 4f}};

        var sub = MatrixUtils.subtractMatrices(a, b);

        assertNotNull(sub);
        assertEquals(5f, sub[0][0]);
        assertEquals(7f, sub[0][1]);
        assertEquals(1f, sub[0][2]);

        assertEquals(-5.5f, sub[1][0]);
        assertEquals(-30f, sub[1][1]);
        assertEquals(-12f, sub[1][2]);

        assertEquals(0f, sub[2][0]);
        assertEquals(-9.25f, sub[2][1]);
        assertEquals(-1f, sub[2][2]);
    }
    @Test
    public void testMultiplyMatricesR2() {
        var a = new float[][]{
                {1, -2f},
                {-3f, 4f}};
        var b = new float[][]{
                {5f, 6f},
                {7f, 8f}};

        var mult = MatrixUtils.multiplyMatrices(a, b);

        assertNotNull(mult);
        assertEquals(2, mult.length);
        assertEquals(2, mult[0].length);
        assertEquals(-9, mult[0][0], 0.001);
        assertEquals(-10f, mult[0][1]);

        assertEquals(13, mult[1][0]);
        assertEquals(14f, mult[1][1]);
    }


    @Test
    public void testMultiplyMatricesR2x2_R2x3() {
        var a = new float[][]{
                {2, -0.6f},
                {3.5f, -32f}};
        var b = new float[][]{
                {4, 65f, 0.34f},
                {9.3f, -7f, 0.32f}};

        var mult = MatrixUtils.multiplyMatrices(a, b);

        assertNotNull(mult);
        assertEquals(2, mult.length);
        assertEquals(3, mult[0].length);
        assertEquals(2.419, mult[0][0], 0.001);
        assertEquals(134.2f, mult[0][1]);
        assertEquals(0.488f, mult[0][2], 0.001);

        assertEquals(-283.6f, mult[1][0]);
        assertEquals(451.5f, mult[1][1]);
        assertEquals(-9.049f, mult[1][2], 0.001);
    }



}
