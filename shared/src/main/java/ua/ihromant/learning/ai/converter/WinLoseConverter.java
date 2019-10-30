package ua.ihromant.learning.ai.converter;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class WinLoseConverter implements QValueConverter {
	private static final double[] WIN = {1, 0};
	private static final double[] LOSE = {0, 1};

	private double convertToQValue(double first) {
		return first;
	}

	private double[] fromQValue(double value) {
		if (value <= 0) {
			return LOSE;
		}
		if (value >= 1) {
			return WIN;
		}
		if (Double.isFinite(value)) {
			return new double[] {value, 1 - value};
		}

		return LOSE; // never, but in case of NAN etc.
	}

	@Override
	public INDArray convertToArray(List<Double> values) {
		double[][] result = new double[values.size()][];
		for (int i = 0; i < result.length; i++) {
			result[i] = fromQValue(values.get(i));
		}
		return Nd4j.create(result);
	}

	@Override
	public List<Double> convertToQValues(INDArray indArray) {
		int length = (int) indArray.shape()[0];
		List<Double> result = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			result.add(convertToQValue(indArray.getDouble(i, 0)));
		}
		return result;
	}

	@Override
	public int outputLength() {
		return 2;
	}
}
