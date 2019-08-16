package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.NimLinePlayer;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.State;

import java.util.function.Supplier;

public class NimLineFactory implements Factory<NimAction> {
	private static final int[] base = {1, 3, 5, 7};
	@Override
	public Supplier<State<NimAction>> getStateSupplier() {
		return () -> new NimLineState(base);
	}

	@Override
	public Agent<NimAction> player() {
		return new NimLinePlayer();
	}

	@Override
	public Agent<NimAction> createAI(String path) {
		return AIZoo.networkNimAI(path);
	}
}
