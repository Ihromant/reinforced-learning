package ua.ihromant.learning.ai.qtable;

public interface NeuralNetworkConverter {
	double convertToQValue(double[] values);
	double[] fromQValue(double value);
	int inputLength();
	int outputLength();
}
