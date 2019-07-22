package ua.ihromant.learning.ai.qtable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.state.Action;

public class NetworkQTable implements QTable {
    private MultiLayerNetwork net = TF.createNetwork();

    @Override
    public double get(Action action) {
        Pair<double[], double[]> pair = Pair.create(action.getTo().toModel(),
                new double[]{0});
        double result = net.output(new DoublesDataSetIterator(Collections.singletonList(
                pair), 1)).getDouble(0);
        return result;
    }

    @Override
    public double[] getMultiple(List<Action> actions) {
        DataSetIterator iter = new DoublesDataSetIterator(
                actions.stream().map(a -> new Pair<>(a.getTo().toModel(),
                        new double[]{0})).collect(Collectors.toList()), actions.size());
        INDArray output = net.output(iter);
        return IntStream.range(0, actions.size())
                .mapToDouble(i -> output.getDouble(i, 0))
                .toArray();
    }

    @Override
    public void set(Action action, double newValue) {
        DataSetIterator iter = new DoublesDataSetIterator(
                Collections.singletonList(
                        Pair.create(action.getTo().toModel(),
                                new double[]{newValue})), 1);
        net.fit(iter);
    }

    @Override
    public void setMultiple(Map<Action, Double> newValues) {
        DataSetIterator iter = new DoublesDataSetIterator(
                newValues.entrySet().stream().map(e -> new Pair<>(e.getKey().getTo().toModel(),
                        new double[]{e.getValue()})).collect(Collectors.toList()), newValues.size());
        net.fit(iter);
    }
}
