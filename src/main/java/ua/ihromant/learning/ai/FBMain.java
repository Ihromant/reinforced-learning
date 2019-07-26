package ua.ihromant.learning.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.ai.qtable.TF1;

public class FBMain {
	private static final Random rand = new Random();
	private static final int INPUT_SIZE = 10;
	private static final int TRAIN_COUNT = 10000;
	private static final int BATCH_SIZE = 100;
	private static final MultiLayerNetwork net = TF1.createNetwork();

	public static void main(String[] args) {
		train();
		IntStream.range(0, 200).forEach(i -> {
			INDArray out = net.output(new DoublesDataSetIterator(Collections.singletonList(Pair.create(convertToInput(i),
					convertToOutput(i))), 1));
			double[] evals = IntStream.range(0, 4).mapToDouble(j -> out.getDouble(0, j)).toArray();
			int maxIndex = IntStream.range(0, evals.length)
					.reduce((a, b) -> evals[a] < evals[b] ? b : a)
					.orElse(-1);
			switch (maxIndex) {
				case 0:
					System.out.println(i + " FB");
					break;
				case 1:
					System.out.println(i + " F");
					break;
				case 2:
					System.out.println(i + " B");
					break;
				case 3:
					System.out.println(i + " " + i);
					break;
				default: throw new IllegalArgumentException();
			}
		});
	}

	private static void train() {
		int percentage = 0;
		for (int i = 0; i < TRAIN_COUNT; i++) {
			if (i == TRAIN_COUNT / 100 * percentage) {
				System.out.println("Learning " + percentage++ + "% complete");
			}
			List<Pair<double[], double[]>> pairs = new ArrayList<>();
			for (int j = 0; j < BATCH_SIZE; j++) {
				int next = rand.nextInt(1000 - 100) + 101;
				pairs.add(Pair.create(convertToInput(next), convertToOutput(next)));
			}
			net.fit(new DoublesDataSetIterator(pairs, BATCH_SIZE));
		}
	}

	private static double[] convertToOutput(int number) {
		if (number % 15 == 0) {
			return new double[]{1, 0, 0, 0};
		}
		if (number % 3 == 0) {
			return new double[]{0, 1, 0, 0};
		}
		if (number % 5 == 0) {
			return new double[]{0, 0, 1, 0};
		}
		return new double[]{0, 0, 0, 1};
	}

	private static double[] convertToInput(int number) {
		double[] result = new double[INPUT_SIZE];
		for (int i = 0; i < INPUT_SIZE; i++) {
			result[INPUT_SIZE - i - 1] = number % 2;
			number = number / 2;
		}
		return result;
	}
}
