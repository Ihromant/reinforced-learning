package ua.ihromant.reinforced.ai.factory;

import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.NimStateConverter;
import ua.ihromant.learning.ai.converter.QValueConverter;
import ua.ihromant.learning.ai.converter.TicTacToeStateConverter;
import ua.ihromant.learning.ai.converter.WinDrawLoseConverter;
import ua.ihromant.learning.ai.converter.WinLoseConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimState;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;
import ua.ihromant.reinforced.ai.qtable.QLearningTemplate;
import ua.ihromant.reinforced.ai.qtable.TrainableQTable;
import ua.ihromant.reinforced.ai.qtable.TrainingAgent;
import ua.ihromant.reinforced.ai.qtable.map.TrainableMapQTable;
import ua.ihromant.reinforced.ai.qtable.network.TrainableNetworkQTable;

import java.util.function.Supplier;

public class TrainerFactory {
    private TrainerFactory() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static TrainingAgent<TTTAction> loadTicTacToeAgent(Supplier<TicTacToeState> supplier, String path) {
        TicTacToeState state = supplier.get();
        return new QLearningTemplate<>(state,
                new TrainableNetworkQTable<>(new TicTacToeStateConverter(state),
                        new WinDrawLoseConverter(), new NeuralNetworkAgent(path)));
    }

    public static TrainingAgent<TTTAction> newTicTacToeAgent(Supplier<TicTacToeState> supplier) {
        TicTacToeState state = supplier.get();
        InputConverter<TTTAction> inputConvert = new TicTacToeStateConverter(state);
        QValueConverter qConvert = new WinDrawLoseConverter();
        return new QLearningTemplate<>(state, newNetworkQTable(inputConvert, qConvert));
    }

    public static TrainingAgent<NimAction> loadNimAgent(Supplier<NimState> supplier, String path) {
        return new QLearningTemplate<>(supplier.get(),
                new TrainableNetworkQTable<>(new NimStateConverter(),
                        new WinLoseConverter(), new NeuralNetworkAgent(path)));
    }

    public static TrainingAgent<NimAction> newNimAgent(Supplier<NimState> supplier) {
        InputConverter<NimAction> inputConvert = new NimStateConverter();
        QValueConverter qConvert = new WinLoseConverter();
        return new QLearningTemplate<>(supplier.get(), newNetworkQTable(inputConvert, qConvert));
    }

    private static <A> TrainableQTable<A> newMapQTable() {
        return new TrainableMapQTable<>(0.3);
    }

    private static <A> TrainableQTable<A> newNetworkQTable(InputConverter<A> inputConverter, QValueConverter qValueConverter) {
        return new TrainableNetworkQTable<>(inputConverter, qValueConverter,
                new NeuralNetworkAgent(inputConverter.buildConfig(qValueConverter.outputLength())));
    }
}
