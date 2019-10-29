package ua.ihromant.reinforced.ai.qtable.network;

import org.nd4j.linalg.api.ndarray.INDArray;
import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.qtable.NetworkQTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.reinforced.ai.qtable.TrainableQTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainableNetworkQTable<A> extends NetworkQTable<A> implements TrainableQTable<A> {
	public TrainableNetworkQTable(InputConverter<A> inputConverter, QValueConverter qConverter,
			NeuralNetworkAgent agent) {
		super(inputConverter, qConverter, agent);
	}

    @Override
    public void set(StateAction<A> stateAction, double newValue) {
		agent.set(inputConverter.convert(Collections.singletonList(stateAction)), qConverter.convertToArray(qConverter.fromQValue(newValue)));
    }

    @Override
    public void setMultiple(Map<StateAction<A>, Double> newValues) {
		INDArray models = inputConverter.convert(new ArrayList<>(newValues.keySet()));
		List<double[]> values = newValues.keySet().stream().map(state -> qConverter.fromQValue(newValues.get(state))).collect(Collectors.toList());
        agent.set(models, values);
    }

	@Override
	public void serialize(String path) {
		agent.serialize(path);
	}
}
