package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToe5x6Player;
import ua.ihromant.learning.ai.qtable.NeuralNetworkConverter;
import ua.ihromant.learning.ai.qtable.WinDrawLoseConverter;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState5x6;

public class TicTacToe5x6Factory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState5x6::new;
	}

	@Override
	public Agent<TTTAction> player(Scanner scan) {
		return new TicTacToe5x6Player(scan);
	}

	@Override
	public NeuralNetworkConverter converter() {
		return new WinDrawLoseConverter(new TicTacToeState5x6().toModel().length);
	}

	@Override
	public int trainingEpisodes() {
		return 1000000;
	}
}
