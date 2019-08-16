package ua.ihromant.learning.factory;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.NimPlayer;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimMultipleState;
import ua.ihromant.learning.state.State;

import java.util.function.Supplier;

public class NimFactory implements Factory<NimAction> {
	private static final int[] base = {1, 3, 5, 7};
	@Override
	public Supplier<State<NimAction>> getStateSupplier() {
		return () -> new NimMultipleState(base);
	}

	@Override
	public Agent<NimAction> player() {
		return new NimPlayer();
	}

	@Override
	public Agent<NimAction> createAI(String path) {
		return AIZoo.networkNimAI(path);
	}
}
