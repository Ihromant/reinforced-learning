package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.GameBoard;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.NetworkQTable;
import ua.ihromant.learning.ai.qtable.converter.InputConverter;
import ua.ihromant.learning.ai.qtable.converter.QValueConverter;
import ua.ihromant.learning.state.State;

public interface Factory<A> {
	Supplier<State<A>> getStateSupplier();

	Agent<A> player(Scanner scan);

	QValueConverter converter();

	InputConverter conv();

	int trainingEpisodes();

	default Agent<A> createAI() {
		return new QLearningTemplate<>(getStateSupplier().get(),
				new NetworkQTable<>(conv(), converter()), trainingEpisodes());
	}

	default GameBoard<A> createBoard() {
		return new GameBoard<>(createAI(), player(new Scanner(System.in)), getStateSupplier());
	}
}
