package ua.ihromant.learning.ai.qtable;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class TF1 {
	private static final MultiLayerConfiguration config = buildGraph();

	private static MultiLayerConfiguration buildGraph() {
		return new NeuralNetConfiguration.Builder()
				.seed(1000)
				.weightInit(WeightInit.XAVIER)
				//.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs())
				.list()
				.layer(0, new DenseLayer.Builder().nIn(10).nOut(100)
						//.weightInit(WeightInit.ZERO)
						.activation(Activation.RELU)
						.build())
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
						.activation(Activation.SOFTMAX)
						.nIn(100).nOut(4).build())
				.build();
	}

	public static MultiLayerNetwork createNetwork() {
		MultiLayerNetwork net = new MultiLayerNetwork(config);
		net.init();
		return net;
	}
}
