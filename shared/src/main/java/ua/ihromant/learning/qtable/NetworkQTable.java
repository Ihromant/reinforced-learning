package ua.ihromant.learning.qtable;

import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;

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
		return qConverter.convertToQValue(agent.get(inputConverter.convert(stateAction), qConverter.outputLength()));
	}

	@Override
	public Map<StateAction<A>, Double> getMultiple(Stream<StateAction<A>> stream) {
		List<StateAction<A>> pairs = stream.collect(Collectors.toList());
		List<double[]> evals = agent.getMultiple(
				pairs.stream().map(inputConverter::convert).collect(Collectors.toList()), qConverter.outputLength());
		return IntStream.range(0, pairs.size()).boxed()
				.collect(Collectors.toMap(pairs::get,
						i -> qConverter.convertToQValue(evals.get(i)),
						(u, v) -> u));
	}
}
