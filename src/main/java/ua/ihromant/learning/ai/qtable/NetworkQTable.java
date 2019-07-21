package ua.ihromant.learning.ai.qtable;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;
import ua.ihromant.learning.state.Action;

import java.util.Collections;

public class NetworkQTable implements QTable {
    private MultiLayerNetwork net = TF.createNetwork();

    @Override
    public double get(Action action) {
        Pair<double[], double[]> pair = Pair.create(action.getTo().toModel(),
                new double[]{0});
        Evaluation eval = net.evaluate(new DoublesDataSetIterator(Collections.singletonList(pair), 1));
        return pair.getSecond()[0];
    }

    @Override
    public void set(Action action, double newValue) {
        DataSetIterator iter = new DoublesDataSetIterator(
                Collections.singletonList(
                        Pair.create(action.getTo().toModel(),
                                new double[]{newValue})), 1);
        net.fit(iter);
    }
}
