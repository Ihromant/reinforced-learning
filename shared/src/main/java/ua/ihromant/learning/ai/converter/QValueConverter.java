package ua.ihromant.learning.ai.converter;

import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface QValueConverter {
	INDArray convertToArray(List<Double> values);
	int outputLength();

	List<Double> convertToQValues(INDArray indArray);
}
