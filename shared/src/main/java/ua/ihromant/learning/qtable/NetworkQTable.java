package ua.ihromant.learning.qtable;

import org.nd4j.linalg.api.ndarray.INDArray;
import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NetworkQTable<A> implements QTable<A> {
	protected final NeuralNetworkAgent agent;
	protected final QValueConverter qConverter;
	protected final InputConverter<A> inputConverter;

	public NetworkQTable(InputConverter<A> inputConverter, QValueConverter qConverter, NeuralNetworkAgent agent) {
		this.qConverter = qConverter;
		this.agent = agent;
		this.inputConverter = inputConverter;
	}

	@Override
	public double get(StateAction<A> stateAction) {
		INDArray input = inputConverter.convert(Collections.singletonList(stateAction));
		return qConverter.convertToQValues(agent.get(input)).get(0);
	}

	@Override
	public Map<StateAction<A>, Double> getMultiple(Stream<StateAction<A>> stream) {
		List<StateAction<A>> stateActions = stream.collect(Collectors.toList());
		INDArray input = inputConverter.convert(stateActions);
		List<Double> evals = qConverter.convertToQValues(agent.get(input));
		return IntStream.range(0, stateActions.size()).boxed()
				.collect(Collectors.toMap(stateActions::get,
						evals::get));
	}
}
