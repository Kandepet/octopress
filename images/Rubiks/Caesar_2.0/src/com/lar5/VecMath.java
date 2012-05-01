package com.lar5;

/**
 * Created lars  May 25, 2003  2:36:54 PM
 *
 * Merging this class into Caesar saves 387 bytes in the jar file. Hardly worth it, But I could do it as a final optimization.
 */
public class VecMath
{
    protected static double scalProd(double v1[], int ix1, double v2[], int ix2)
    {
        return v1[ix1]*v2[ix2] + v1[ix1 + 1]*v2[ix2 + 1] + v1[ix1 + 2]*v2[ix2 + 2];
    }

    protected static double vNorm(double v[], int ix)
    {
        return Math.sqrt(v[ix]*v[ix] + v[ix + 1]*v[ix + 1] + v[ix + 2]*v[ix + 2]);
    }

    protected static double cosAng(double v1[], double v2[], int ix2)
    {
        return scalProd(v1, 0, v2, ix2)/(vNorm(v1, 0)*vNorm(v2, ix2));
    }

    protected static void normalize(double v[])
    {
        double t = vNorm(v, 0);
        v[0] = v[0]/t;
        v[1] = v[1]/t;
        v[2] = v[2]/t;
    }

    protected static void scalMult(double v[], double a)
    {
        v[0] = v[0]*a;
        v[1] = v[1]*a;
        v[2] = v[2]*a;
    }

    /**
     * v2 = v2 + v1
     */
    protected static void add(double v1[], double v2[], int ix2)
    {
        v2[ix2]     += v1[0];
        v2[ix2 + 1] += v1[1];
        v2[ix2 + 2] += v1[2];
    }

    protected static void sub(double v1[], double v2[], int ix2)
    {
        v2[ix2]     -= v1[0];
        v2[ix2 + 1] -= v1[1];
        v2[ix2 + 2] -= v1[2];
    }

    protected static void copy(double v1[], int ix1, double v2[])
    {
        v2[0] = v1[ix1];
        v2[1] = v1[ix1 + 1];
        v2[2] = v1[ix1 + 2];
    }

    /**
     * v3 = v1 * v2
     */
    protected static void vecProd(double v1[], double v2[], double v3[])
    {
        v3[0] = v1[1]*v2[2] - v1[2]*v2[1];
        v3[1] = v1[2]*v2[0] - v1[0]*v2[2];
        v3[2] = v1[0]*v2[1] - v1[1]*v2[0];
    }


}
