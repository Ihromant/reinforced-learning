package ua.ihromant.learning.qtable;

import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.state.State;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NetworkQTable<A> implements QTable<A> {
	protected final NeuralNetworkAgent agent;
	protected final QValueConverter qConverter;
	protected final InputConverter<State<A>> inputConverter;

	public NetworkQTable(InputConverter<State<A>> inputConverter, QValueConverter qConverter, NeuralNetworkAgent agent) {
		this.qConverter = qConverter;
		this.agent = agent;
		this.inputConverter = inputConverter;
	}

	@Override
	public double get(State<A> state) {
		return qConverter.convertToQValue(agent.get(inputConverter.convert(state), qConverter.outputLength()));
	}

	@Override
	public Map<State<A>, Double> getMultiple(Stream<State<A>> stream) {
		List<State<A>> states = stream.collect(Collectors.toList());
		List<double[]> evals = agent.getMultiple(
				states.stream().map(inputConverter::convert).collect(Collectors.toList()), qConverter.outputLength());
		return IntStream.range(0, states.size()).boxed()
				.collect(Collectors.toMap(states::get,
						i -> qConverter.convertToQValue(evals.get(i)),
						(u, v) -> u));
	}
}
