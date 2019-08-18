package ua.ihromant.learning.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class ProbabilityUtilTest {
    private static final double ERROR = 1e-7;
    @Test
    public void testCalculateExploration() {
        IntStream.range(9, 100).forEach(maxMoves ->
                Assertions.assertEquals(
                        0.8,
                        calculateNotRandom(ProbabilityUtil.calculateExploration(0, maxMoves), maxMoves),
                        ERROR));

    }

    private static double calculateNotRandom(double probability, double maxMoves) {
        return 1 - Math.pow(1 - probability, 2 * Math.sqrt(maxMoves));
    }
}
