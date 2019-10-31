package ua.ihromant.learning.ai.converter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;

public class TicTacToeStateConverter implements InputConverter<TTTAction> {
	private final TicTacToeState state;
	public TicTacToeStateConverter(TicTacToeState state) {
		this.state = state;
	}

	private double[][][] convert(StateAction<TTTAction> stateAction) {
		TicTacToeState state = stateAction.getResult();
		double[][][] res = new double[3][state.horSize()][state.verSize()];
		IntStream.range(0, state.getMaximumMoves())
				.forEach(i -> {
					Player current = state.getCurrent();
					Player pl = state.getPlayer(i);
					res[pl == null ? 2 : Math.abs(current.ordinal() - pl.ordinal())][i / state.verSize()][i % state.verSize()] = 1;
				});
		return res;
	}

	@Override
	public INDArray convert(List<StateAction<TTTAction>> stateActions) {
		double[][][][] result = new double[stateActions.size()][][][];
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
				.layer(0, new ConvolutionLayer.Builder(state.winLength(), state.winLength())
						//nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
						.nIn(3)
						.nOut(20)
						.activation(Activation.IDENTITY)
						.build())
				.layer(1, new GlobalPoolingLayer.Builder(PoolingType.MAX)
						.build())
				.layer(2, new DenseLayer.Builder()
						.nIn(20)
						.nOut(40)
						.activation(Activation.RELU)
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(5)
						.build())
				.layer(3, new OutputLayer
						.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
						.activation(Activation.SOFTMAX)
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(5)
						.nIn(40)
						.nOut(outputLength).build())
				.build();
	}
}
