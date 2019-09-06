package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.MinimaxAI;
import ua.ihromant.learning.ai.QLearningAI;
import ua.ihromant.learning.ai.converter.NimStateConverter;
import ua.ihromant.learning.ai.converter.TicTacToeStateConverter;
import ua.ihromant.learning.ai.converter.WinDrawLoseConverter;
import ua.ihromant.learning.ai.converter.WinLoseConverter;
import ua.ihromant.learning.network.NeuralNetworkAgent;
import ua.ihromant.learning.qtable.MapQTable;
import ua.ihromant.learning.qtable.NetworkQTable;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;

public class AIZoo {
    private AIZoo() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static <A> Agent<A> miniMax(Player pl) {
        return new MinimaxAI<>(pl);
    }

    public static <A> Agent<A> mapQTableAI() {
        return new QLearningAI<>(new MapQTable<>(null)); // TODO
    }

    public static QLearningAI<TTTAction> networkTTTAI(int size, String model) {
        return new QLearningAI<>(new NetworkQTable<>(new TicTacToeStateConverter(size),
                new WinDrawLoseConverter(),
                new NeuralNetworkAgent(model)));
    }

    public static QLearningAI<NimAction> networkNimAI(String model) {
        return new QLearningAI<>(new NetworkQTable<>(new NimStateConverter(),
                new WinLoseConverter(),
                new NeuralNetworkAgent(model)));
    }
}
