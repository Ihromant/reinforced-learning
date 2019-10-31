package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToe5x5Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState5x5;

import java.util.function.Supplier;

public class TicTacToe5x5Factory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState5x5::new;
	}

	@Override
	public Agent<TTTAction> player() {
		return new TicTacToe5x5Player();
	}

	@Override
	public Agent<TTTAction> createAI(String path) {
		return AIZoo.networkTTTAI(new TicTacToeState5x5(), path);
	}
}
