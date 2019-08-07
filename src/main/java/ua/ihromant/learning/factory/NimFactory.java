package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.NimPlayer;
import ua.ihromant.learning.ai.qtable.NeuralNetworkConverter;
import ua.ihromant.learning.ai.qtable.WinLoseConverter;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimState;
import ua.ihromant.learning.state.State;

public class NimFactory implements Factory<NimAction> {
	int[] base = {1, 3, 5, 7};
	@Override
	public Supplier<State<NimAction>> getStateSupplier() {
		return () -> new NimState(base);
	}

	@Override
	public Agent<NimAction> player(Scanner scan) {
		return new NimPlayer(scan);
	}

	@Override
	public NeuralNetworkConverter converter() {
		return new WinLoseConverter(getStateSupplier().get().toModel().length);
	}

	@Override
	public int trainingEpisodes() {
		return 300000;
	}
}
