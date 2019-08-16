package ua.ihromant.learning.state;

public class NimAction {
    private final int[] coeffs;
    private final int reduce;

    public NimAction(int[] coeffs, int reduce) {
        this.coeffs = coeffs;
        this.reduce = reduce;
    }

    public NimAction(int coef, int reduce) {
        this(new int[]{coef}, reduce);
    }

    public int[] getCoeffs() {
        return coeffs;
    }

    public int getReduce() {
        return reduce;
    }
}
