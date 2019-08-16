package ua.ihromant.learning.network;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Pair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NeuralNetworkAgent {
    private final MultiLayerNetwork network;

    public NeuralNetworkAgent(MultiLayerConfiguration config) {
        this.network = new MultiLayerNetwork(config);
        this.network.init();
    }

    public NeuralNetworkAgent(String path) {
        try {
            this.network = ModelSerializer.restoreMultiLayerNetwork(path);
        } catch (IOException e) {
            throw new RuntimeException("Was not able to restore model from path: " + path, e);
        }
    }

    public double[] get(double[] model, int outputLength) {
        Pair<double[], double[]> pair = Pair.create(model, new double[]{outputLength});
        INDArray output = network.output(new DoublesDataSetIterator(Collections.singletonList(
                pair), 1));
        return IntStream.range(0, outputLength).mapToDouble(j -> output.getDouble(0, j)).toArray();
    }

    public List<double[]> getMultiple(List<double[]> models, int outputLength) {
        DataSetIterator iter = new DoublesDataSetIterator(
                models.stream().map(mod -> new Pair<>(mod,
                        new double[outputLength])).collect(Collectors.toList()),
                models.size());
        INDArray output = network.output(iter);
        return IntStream.range(0, models.size())
                .mapToObj(i -> IntStream.range(0, outputLength)
                        .mapToDouble(j -> output.getDouble(i, j)).toArray()).collect(Collectors.toList());
    }

    public void set(double[] model, double[] newValue) {
        DataSetIterator iter = new DoublesDataSetIterator(
                Collections.singletonList(Pair.create(model, newValue)), 1);
        network.fit(iter);
    }

    public void setMultiple(List<double[]> models, List<double[]> values) {
        if (models.size() != values.size()) {
            throw new IllegalArgumentException("Sizes of models and values are not the same");
        }

        DataSetIterator iter = new DoublesDataSetIterator(
                IntStream.range(0, models.size())
                        .mapToObj(i -> new Pair<>(models.get(i),
                        values.get(i))).collect(Collectors.toList()), models.size());
        network.fit(iter);
    }

    public void serialize(String path) {
        try {
            ModelSerializer.writeModel(this.network, path, true);
        } catch (IOException e) {
            throw new RuntimeException("Was not able to persist model to path: " + path, e);
        }
    }
}