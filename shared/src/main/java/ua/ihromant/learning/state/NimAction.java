package ua.ihromant.learning.state;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class NimAction implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NimAction nimAction = (NimAction) o;
        return reduce == nimAction.reduce &&
                Arrays.equals(coeffs, nimAction.coeffs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(reduce);
        result = 31 * result + Arrays.hashCode(coeffs);
        return result;
    }
}
