package ua.ihromant.learning.network;

import java.io.IOException;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

public class NeuralNetworkAgent {
    private final MultiLayerNetwork network;

    public NeuralNetworkAgent(MultiLayerConfiguration config) {
        this.network = new MultiLayerNetwork(config);
        this.network.init();
        listener();
    }

    public NeuralNetworkAgent(String path) {
        try {
            this.network = ModelSerializer.restoreMultiLayerNetwork(path);
        } catch (IOException e) {
            throw new RuntimeException("Was not able to restore model from path: " + path, e);
        }
        listener();
    }

    private void listener() {
//        UIServer uiServer = UIServer.getInstance();
//
//        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
//        StatsStorage statsStorage = new InMemoryStatsnStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
//
//        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
//        uiServer.attach(statsStorage);
//
//        //Then add the StatsListener to collect this information from the network, as it trains
//        this.network.setListeners(new StatsListener(statsStorage));
    }

    public INDArray get(INDArray input) {
        return network.output(input);
    }

    public void set(INDArray input, INDArray result) {
        network.fit(input, result);
    }

    public void serialize(String path) {
        try {
            ModelSerializer.writeModel(this.network, path, true);
        } catch (IOException e) {
            throw new RuntimeException("Was not able to persist model to path: " + path, e);
        }
    }
}
