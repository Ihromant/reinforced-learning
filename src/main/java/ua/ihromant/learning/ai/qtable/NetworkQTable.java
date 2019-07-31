package ua.ihromant.learning.ai.qtable;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.state.State;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkQTable<A> implements QTable<A> {
    private MultiLayerNetwork net = TF.createNetwork();

    @Override
    public double get(State<A> state) {
        Pair<double[], double[]> pair = Pair.create(state.toModel(),
                new double[]{0});
        double result = net.output(new DoublesDataSetIterator(Collections.singletonList(
                pair), 1)).getDouble(0);
        return result;
    }

    @Override
    public double[] getMultiple(List<State<A>> states) {
        DataSetIterator iter = new DoublesDataSetIterator(
                states.stream().map(st -> new Pair<>(st.toModel(),
                        new double[]{0})).collect(Collectors.toList()), states.size());
        INDArray output = net.output(iter);
        return IntStream.range(0, states.size())
                .mapToDouble(i -> output.getDouble(i, 0))
                .toArray();
    }

    @Override
    public void set(State<A> state, double newValue) {
        DataSetIterator iter = new DoublesDataSetIterator(
                Collections.singletonList(
                        Pair.create(state.toModel(),
                                new double[]{newValue})), 1);
        net.fit(iter);
    }

    @Override
    public void setMultiple(Map<State<A>, Double> newValues) {
        DataSetIterator iter = new DoublesDataSetIterator(
                newValues.entrySet().stream().map(e -> new Pair<>(e.getKey().toModel(),
                        new double[]{e.getValue()})).collect(Collectors.toList()), newValues.size());
        net.fit(iter);
    }

	@Override
	public double getMax(List<State<A>> states) {
		double rewardMax = states.stream().mapToDouble(State::getUtility).max().orElse(0.0);
		if (rewardMax > 0.0) {
			return rewardMax;
		}
		double[] evals = getMultiple(states);
		int maxIndex = IntStream.range(0, evals.length)
				.reduce((a, b) -> evals[a] < evals[b] ? b : a)
				.orElse(-1);
		return maxIndex == -1 ? 0.0 : evals[maxIndex];
	}

	@Override
	public State<A> getMaxAction(List<State<A>> actions) {
		double[] results = getMultiple(actions);
		return actions.get(IntStream.range(0, results.length)
				.reduce((a, b) -> results[a] < results[b] ? b : a).orElse(0));
	}
}
