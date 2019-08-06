package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.GameBoard;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.NetworkQTable;
import ua.ihromant.learning.ai.qtable.NeuralNetworkConverter;
import ua.ihromant.learning.state.State;

public interface Factory<A> {
	Supplier<State<A>> getStateSupplier();

	Agent<A> player(Scanner scan);

	NeuralNetworkConverter converter();

	int trainingEpisodes();

	default Agent<A> createAI() {
		return new QLearningTemplate<>(getStateSupplier().get(),
				new NetworkQTable<>(converter()), trainingEpisodes());
	}

	default GameBoard<A> createBoard() {
		return new GameBoard<>(createAI(), player(new Scanner(System.in)), getStateSupplier());
	}
}
