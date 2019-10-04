package ua.ihromant.reinforced.ai.qtable.network;

import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.qtable.NetworkQTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.reinforced.ai.qtable.TrainableQTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class TrainableNetworkQTable<A> extends NetworkQTable<A> implements TrainableQTable<A> {
	private Map<StateAction<A>, List<double[]>> cached = new HashMap<>();
	public TrainableNetworkQTable(InputConverter<A> inputConverter, QValueConverter qConverter,
			NeuralNetworkAgent agent) {
		super(inputConverter, qConverter, agent);
	}

    @Override
    public void set(StateAction<A> stateAction, double newValue) {
		agent.set(inputConverter.convert(stateAction), qConverter.fromQValue(newValue));
    }

    @Override
    public void setMultiple(Map<StateAction<A>, Double> newValues) {
		List<double[]> models = newValues.keySet().stream().map(inputConverter::convert).collect(Collectors.toList());
		List<double[]> values = newValues.keySet().stream().map(state -> qConverter.fromQValue(newValues.get(state))).collect(Collectors.toList());
        agent.setMultiple(models, values);
        // debug
	    IntStream.range(0, models.size()).forEach(i -> cached.compute(inputConverter.reverse(models.get(i)),
			    (k, v) -> {List<double[]> l = v == null ? new ArrayList<>() : v;
				    if (l.size() < 300) {
					    l.add(values.get(i));
				    }
				    return l;
			    }));
    }

	@Override
	public void serialize(String path) {
		agent.serialize(path);
		// debug
		cached.entrySet().stream()
				.filter(e -> e.getValue().size() > 200)
				.filter(e -> e.getKey().getResult().getExpectedResult(Player.X) != GameResult.LOSE)
				.forEach(e -> System.out.println(e.getKey() + " -> " +
				IntStream.range(0, e.getValue().size())
						.mapToObj(i -> Arrays.toString(DoubleStream.concat(DoubleStream.of(i),
								Arrays.stream(e.getValue().get(i))).toArray()))
						.collect(Collectors.joining(","))));
	}
}
