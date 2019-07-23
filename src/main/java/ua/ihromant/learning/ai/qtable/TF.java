package ua.ihromant.learning.ai.qtable;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class TF {
	private static final MultiLayerConfiguration config = buildGraph();

	private static MultiLayerConfiguration buildGraph() {
		return new NeuralNetConfiguration.Builder()
				.seed(1000)
				.updater(new Adam())
				.list()
				.layer(0, new DenseLayer.Builder().nIn(27).nOut(1000)
						.weightInit(WeightInit.ZERO)
						.activation(Activation.IDENTITY)
						.build())
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
						.activation(Activation.IDENTITY)
						.nIn(1000).nOut(1).build())
				.build();
	}

	public static MultiLayerNetwork createNetwork() {
	    MultiLayerNetwork net = new MultiLayerNetwork(config);
	    net.init();
	    return net;
	}
}
