package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToe5x6Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState5x6;

import java.util.function.Supplier;

public class TicTacToe5x6Factory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState5x6::new;
	}

	@Override
	public Agent<TTTAction> player() {
		return new TicTacToe5x6Player();
	}

	@Override
	public Agent<TTTAction> createAI(String path) {
		return AIZoo.networkTTTAI(new TicTacToeState5x6(), path);
	}
}
