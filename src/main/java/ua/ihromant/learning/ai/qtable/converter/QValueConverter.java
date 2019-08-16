package ua.ihromant.learning.ai.qtable.converter;

public interface QValueConverter {
	double convertToQValue(double[] values);
	double[] fromQValue(double value);
	int outputLength();
}
