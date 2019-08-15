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
		double rand = ThreadLocalRandom.current().nextDouble(Arrays.stream(weights).sum());
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
}
