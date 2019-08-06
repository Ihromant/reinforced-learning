package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToeSizedPlayer;
import ua.ihromant.learning.ai.qtable.NeuralNetworkConverter;
import ua.ihromant.learning.ai.qtable.WinDrawLoseConverter;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeStateSized;

public class TicTacToeSizedFactory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeStateSized::new;
	}

	@Override
	public Agent<TTTAction> player(Scanner scan) {
		return new TicTacToeSizedPlayer(scan);
	}

	@Override
	public NeuralNetworkConverter converter() {
		return new WinDrawLoseConverter(new TicTacToeStateSized().toModel().length);
	}

	@Override
	public int trainingEpisodes() {
		return 300000;
	}
}
