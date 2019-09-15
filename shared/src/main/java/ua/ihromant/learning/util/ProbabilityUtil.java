package ua.ihromant.learning.util;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ProbabilityUtil {
	private ProbabilityUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static double[] getOneTimeProbabilities(int moves, double conservativeRate) {
		double one = (1.0 - conservativeRate) / moves;
		double[] result = new double[moves];
		double multiplier = 1.0;
		for (int i = 0; i < moves; i++) {
			result[i] = one / multiplier;
			multiplier = multiplier * (1 - result[i]);
		}
		return result;
	}

	public static int weightedRandom(double[] weights) {
		double total = Arrays.stream(weights).sum();
		if (total == 0.0) {
			return ThreadLocalRandom.current().nextInt(weights.length);
		}
		double rand = ThreadLocalRandom.current().nextDouble(total);
		double sum = 0.0;
		int length = weights.length;
		for (int i = 0; i < length; i++) {
			sum += weights[i];
			if (sum > rand) {
				return i;
			}
		}
		return length - 1;
	}

	private static final double A = -0.001;
	private static final double B = 0.8;
	private static final double C = 0.69;

	protected static double notRandomFunction(double consSize) {
		double result = (A * consSize + B) / (C * consSize + 1);
		if (result > 0.8) {
			return 0.8;
		}
		return Math.max(result, 0.01);
	}

	public static double calculateExploration(int consSize, int maxMoves) {
		return 1 - Math.pow(1 - notRandomFunction(consSize), 0.5 / Math.sqrt(maxMoves));
	}
}
