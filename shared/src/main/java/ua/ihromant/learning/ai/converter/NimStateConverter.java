package ua.ihromant.learning.ai.converter;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.NimState;
import ua.ihromant.learning.state.Player;

public class NimStateConverter implements InputConverter<NimAction> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;

	@Override
	public double[] convert(StateAction<NimAction> stateAction) {
		NimState state = stateAction.getState();
		int[] piles = state.getPiles();
		NimAction action = stateAction.getAction();
		Arrays.stream(action.getCoeffs()).filter(i -> piles[i] < action.getReduce())
				.findAny().ifPresent(i -> {
			throw new IllegalArgumentException(String.valueOf(stateAction));
		});
		double[] result = new double[inputLength()];
		for (int i = 0; i < PILES_MAX && i < piles.length; i++) {
			char[] binary = Integer.toBinaryString(piles[i]).toCharArray();
			for (int j = 0; j < binary.length && j < BINARY_NUMBERS; j++) {
				result[i * BINARY_NUMBERS + BINARY_NUMBERS - 1 - j] = binary[binary.length - 1 - j] - '0';
			}
		}
		for (int i : action.getCoeffs()) {
			result[PILES_MAX * BINARY_NUMBERS + i] = 1;
		}
		char[] binary = Integer.toBinaryString(action.getReduce()).toCharArray();
		for (int i = 0; i < binary.length; i++) {
			result[PILES_MAX * BINARY_NUMBERS + PILES_MAX + BINARY_NUMBERS - 1 - i] =
					binary[binary.length - 1 - i] - '0';
		}
		return result;
	}

	@Override
	public MultiLayerConfiguration buildConfig(int outputLength) {
		return new NeuralNetConfiguration.Builder()
				.seed(ThreadLocalRandom.current().nextLong())
				.weightInit(WeightInit.XAVIER)
				.updater(new Adam())
				.list()
				.layer(0, new DenseLayer.Builder()
						.nIn(inputLength())
						.nOut(inputLength() * 10)
						.activation(Activation.RELU)
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(5)
						.build())
				.layer(1, new OutputLayer
						.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
						.activation(Activation.SOFTMAX)
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(5)
						.nIn(inputLength() * 10)
						.nOut(outputLength).build())
				.build();
	}

	private int inputLength() {
		return PILES_MAX * BINARY_NUMBERS + BINARY_NUMBERS + PILES_MAX;
	}
}
