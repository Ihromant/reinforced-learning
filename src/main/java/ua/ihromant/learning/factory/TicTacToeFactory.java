package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToePlayer;
import ua.ihromant.learning.ai.qtable.converter.InputConverter;
import ua.ihromant.learning.ai.qtable.converter.QValueConverter;
import ua.ihromant.learning.ai.qtable.WinDrawLoseConverter;
import ua.ihromant.learning.ai.qtable.converter.TicTacToeStateConverter;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;

public class TicTacToeFactory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState::new;
	}

	@Override
	public Agent<TTTAction> player(Scanner scan) {
		return new TicTacToePlayer(scan);
	}

	@Override
	public QValueConverter converter() {
		return new WinDrawLoseConverter();
	}

	@Override
	public InputConverter conv() {
		return new TicTacToeStateConverter(9);
	}

	@Override
	public int trainingEpisodes() {
		return 300000;
	}
}
