package ua.ihromant.learning.ai.converter;

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
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;

public class TicTacToeStateConverter implements InputConverter<TTTAction> {
	private final int size;
	public TicTacToeStateConverter(int size) {
		this.size = size;
	}

	@Override
	public double[] convert(StateAction<TTTAction> stateAction) {
		TicTacToeState state = stateAction.getResult();
		double[] res = new double[inputLength()];
		IntStream.range(0, size)
				.forEach(i -> {
					Player pl = state.getPlayer(i);
					if (pl == Player.X) {
						res[i] = 1;
					}
					if (pl == Player.O) {
						res[i + size] = 1;
					}
					if (pl == null) {
						res[i + 2 * size] = 1;
					}
				});
		res[res.length - 1] = state.getCurrent().ordinal();
		return res;
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

	@Override
	public int inputLength() {
		return size * 3 + 1;
	}
}
