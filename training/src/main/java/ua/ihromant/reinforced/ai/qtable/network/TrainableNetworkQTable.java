package ua.ihromant.reinforced.ai.qtable.network;

import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.qtable.NetworkQTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.State;
import ua.ihromant.reinforced.ai.qtable.TrainableQTable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainableNetworkQTable<A> extends NetworkQTable<A> implements TrainableQTable<A> {
	public TrainableNetworkQTable(InputConverter<State<A>, A> inputConverter, QValueConverter qConverter,
			NeuralNetworkAgent agent) {
		super(inputConverter, qConverter, agent);
	}

    @Override
    public void set(StateAction<State<A>, A> stateAction, double newValue) {
		agent.set(inputConverter.convert(stateAction), qConverter.fromQValue(newValue));
    }

    @Override
    public void setMultiple(Map<StateAction<State<A>, A>, Double> newValues) {
		List<double[]> models = newValues.keySet().stream().map(inputConverter::convert).collect(Collectors.toList());
		List<double[]> values = newValues.keySet().stream().map(state -> qConverter.fromQValue(newValues.get(state))).collect(Collectors.toList());
        agent.setMultiple(models, values);
    }

	@Override
	public void serialize(String path) {
		agent.serialize(path);
	}
}
