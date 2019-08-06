package ua.ihromant.learning.ai.qtable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.state.State;

public class NetworkQTable<A> implements QTable<A> {
    private final MultiLayerNetwork net;
    private final NeuralNetworkConverter converter;

    public NetworkQTable(NeuralNetworkConverter converter) {
    	this.converter = converter;
    	this.net = TF.createNetwork(converter);
    }

    @Override
    public double get(State<A> state) {
        Pair<double[], double[]> pair = Pair.create(state.toModel(),
                new double[]{0});
	    return net.output(new DoublesDataSetIterator(Collections.singletonList(
	            pair), 1)).getDouble(0);
    }

	@Override
	public Map<State<A>, Double> getMultiple(Stream<State<A>> stream) {
		List<State> states = stream.collect(Collectors.toList());
		DataSetIterator iter = new DoublesDataSetIterator(
				states.stream().map(st -> new Pair<>(st.toModel(),
						new double[converter.outputLength()])).collect(Collectors.toList()),
				states.size());
		INDArray output = net.output(iter);
		return IntStream.range(0, states.size()).boxed()
				.collect(Collectors.toMap(states::get,
						i -> converter.convertToQValue(
								IntStream.range(0, converter.outputLength())
										.mapToDouble(j -> output.getDouble(i, j)).toArray()),
						(u, v) -> u));
	}

    @Override
    public void set(State<A> state, double newValue) {
        DataSetIterator iter = new DoublesDataSetIterator(
                Collections.singletonList(
                        Pair.create(state.toModel(),
                                converter.fromQValue(newValue))), 1);
        net.fit(iter);
    }

    @Override
    public void setMultiple(Map<State<A>, Double> newValues) {
        DataSetIterator iter = new DoublesDataSetIterator(
                newValues.entrySet().stream().map(e -> new Pair<>(e.getKey().toModel(),
                        converter.fromQValue(e.getValue()))).collect(Collectors.toList()), newValues.size());
        net.fit(iter);
    }
}
