package ua.ihromant.learning.ai.converter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimState;

public class NimStateConverter implements InputConverter<NimAction> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;

	private static int[] toBinary(int from, int bitCount) {
		int[] result = new int[bitCount];
		for (int i = 0; i < bitCount; i++) {
			result[bitCount - i - 1] = (from >> i) & 1;
		}
		return result;
	}

	private double[] convert(StateAction<NimAction> stateAction) {
		NimState state = stateAction.getState();
		int[] piles = Arrays.copyOf(state.getPiles(), state.getPiles().length);
		NimAction action = stateAction.getAction();
		Arrays.stream(action.getCoeffs()).filter(i -> piles[i] < action.getReduce())
				.findAny().ifPresent(i -> {
			throw new IllegalArgumentException(String.valueOf(stateAction));
		});
		for (int coeff : action.getCoeffs()) {
			piles[coeff] -= action.getReduce();
		}
		return Arrays.stream(piles)
				.flatMap(i -> Arrays.stream(toBinary(i, BINARY_NUMBERS)))
				.mapToDouble(i -> (double) i)
				.toArray();
	}

	@Override
	public INDArray convert(List<StateAction<NimAction>> stateActions) {
		double[][] result = new double[stateActions.size()][];
		for (int i = 0; i < result.length; i++) {
			result[i] = convert(stateActions.get(i));
		}
		return Nd4j.create(result);
	}

	@Override
	public MultiLayerConfiguration buildConfig(int outputLength) {
		return new NeuralNetConfiguration.Builder()
				.seed(ThreadLocalRandom.current().nextLong())
				.weightInit(WeightInit.XAVIER)
				.updater(new Adam())
				.list()
				.layer(0, new DenseLayer.Builder()
						.activation(Activation.RELU)
						.nIn(inputLength())
						.nOut(inputLength() * 10)
						.build())
				.layer(1, new OutputLayer
						.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
						.activation(Activation.SOFTMAX)
						.nIn(inputLength() * 10)
						.nOut(outputLength).build())
				.build();
	}

	private int inputLength() {
		return PILES_MAX * BINARY_NUMBERS;
	}
}
