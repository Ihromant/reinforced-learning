package ua.ihromant.learning.ai.qtable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.ai.qtable.converter.InputConverter;
import ua.ihromant.learning.ai.qtable.converter.QValueConverter;
import ua.ihromant.learning.state.State;

public class NetworkQTable<A> implements QTable<A> {
    private final MultiLayerNetwork net;
    private final QValueConverter converter;
    private final InputConverter inputConverter;

    public NetworkQTable(InputConverter<State<A>> inputConverter, QValueConverter converter) {
    	this.converter = converter;
    	this.net = TF.createNetwork(inputConverter, converter);
    	this.inputConverter = inputConverter;
    }

	public NetworkQTable(InputConverter inputConverter, QValueConverter converter, String path) throws IOException {
		this.converter = converter;
		this.net = ModelSerializer.restoreMultiLayerNetwork(path);
		this.inputConverter = inputConverter;
	}

    @Override
    public double get(State<A> state) {
        Pair<double[], double[]> pair = Pair.create(inputConverter.convert(state),
                new double[]{0});
	    return net.output(new DoublesDataSetIterator(Collections.singletonList(
	            pair), 1)).getDouble(0);
    }

	@Override
	public Map<State<A>, Double> getMultiple(Stream<State<A>> stream) {
		List<State> states = stream.collect(Collectors.toList());
		DataSetIterator iter = new DoublesDataSetIterator(
				states.stream().map(st -> new Pair<>(inputConverter.convert(st),
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
                        Pair.create(inputConverter.convert(state),
                                converter.fromQValue(newValue))), 1);
        net.fit(iter);
    }

    @Override
    public void setMultiple(Map<State<A>, Double> newValues) {
        DataSetIterator iter = new DoublesDataSetIterator(
                newValues.entrySet().stream().map(e -> new Pair<>(inputConverter.convert(e.getKey()),
                        converter.fromQValue(e.getValue()))).collect(Collectors.toList()), newValues.size());
        net.fit(iter);
    }

	@Override
	public void serialize(String fileName) throws IOException {
		ModelSerializer.writeModel(net, fileName, true);
	}
}
