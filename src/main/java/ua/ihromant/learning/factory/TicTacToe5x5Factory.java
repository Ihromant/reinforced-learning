package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToe5x5Player;
import ua.ihromant.learning.ai.qtable.NeuralNetworkConverter;
import ua.ihromant.learning.ai.qtable.WinDrawLoseConverter;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState5x5;

public class TicTacToe5x5Factory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState5x5::new;
	}

	@Override
	public Agent<TTTAction> player(Scanner scan) {
		return new TicTacToe5x5Player(scan);
	}

	@Override
	public NeuralNetworkConverter converter() {
		return new WinDrawLoseConverter(new TicTacToeState5x5().toModel().length);
	}

	@Override
	public int trainingEpisodes() {
		return 100000;
	}
}
