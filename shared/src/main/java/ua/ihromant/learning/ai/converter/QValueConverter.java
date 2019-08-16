package ua.ihromant.learning.ai.converter;

public interface QValueConverter {
	double convertToQValue(double[] values);
	double[] fromQValue(double value);
	int outputLength();
}
