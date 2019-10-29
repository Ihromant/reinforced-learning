package ua.ihromant.learning.ai.converter;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import ua.ihromant.learning.qtable.StateAction;

import java.util.List;

public interface InputConverter<A> {
	INDArray convert(List<StateAction<A>> stateActions);

	MultiLayerConfiguration buildConfig(int outputLength);
}
