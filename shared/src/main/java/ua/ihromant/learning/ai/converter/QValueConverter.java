package ua.ihromant.learning.ai.converter;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface QValueConverter {
	INDArray convertToArray(double[] values);
	double[] fromQValue(double value);
	int outputLength();
}
