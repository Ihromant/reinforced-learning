package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToePlayer;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState3x3;

import java.util.function.Supplier;

public class TicTacToeFactory implements Factory<TTTAction> {
	@Override
	public Supplier<State<TTTAction>> getStateSupplier() {
		return TicTacToeState3x3::new;
	}

	@Override
	public Agent<TTTAction> player() {
		return new TicTacToePlayer();
	}

	@Override
	public Agent<TTTAction> createAI(String path) {
		return AIZoo.networkTTTAI(getStateSupplier().get().getMaximumMoves(), path);
	}
}
