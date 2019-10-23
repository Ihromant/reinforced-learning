package ua.ihromant.learning.ai.converter;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import ua.ihromant.learning.qtable.StateAction;

public interface InputConverter<A> {
	double[] convert(StateAction<A> stateAction);

	MultiLayerConfiguration buildConfig(int outputLength);

	int inputLength();
}
