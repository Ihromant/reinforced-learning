package ua.ihromant.learning.ai.converter;

import ua.ihromant.learning.state.State;

public interface InputConverter<ST extends State<?>> {
	double[] convert(ST state);
	int inputLength();
}
