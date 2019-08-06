package ua.ihromant.learning.ai.qtable;

import java.util.concurrent.ThreadLocalRandom;

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
	private static MultiLayerConfiguration buildGraph(NeuralNetworkConverter converter) {
		return new NeuralNetConfiguration.Builder()
				.seed(ThreadLocalRandom.current().nextLong())
				.weightInit(WeightInit.XAVIER)
				.updater(new Adam())
				.list()
				.layer(0, new DenseLayer.Builder()
						.nIn(converter.inputLength())
						.nOut(converter.inputLength() * 10)
						.activation(Activation.RELU)
						.build())
				.layer(1, new OutputLayer
						.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
						.activation(Activation.SOFTMAX)
						.nIn(converter.inputLength() * 10)
						.nOut(converter.outputLength()).build())
				.build();
	}

	public static MultiLayerNetwork createNetwork(NeuralNetworkConverter converter) {
	    MultiLayerNetwork net = new MultiLayerNetwork(buildGraph(converter));
	    net.init();
	    return net;
	}
}
