package ua.ihromant.learning.factory;

import java.util.Scanner;
import java.util.function.Supplier;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.NimPlayer;
import ua.ihromant.learning.ai.qtable.converter.InputConverter;
import ua.ihromant.learning.ai.qtable.converter.NimStateConverter;
import ua.ihromant.learning.ai.qtable.converter.QValueConverter;
import ua.ihromant.learning.ai.qtable.WinLoseConverter;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimMultipleState;
import ua.ihromant.learning.state.State;

public class NimFactory implements Factory<NimAction> {
	private static final int[] base = {1, 3, 5, 7};
	@Override
	public Supplier<State<NimAction>> getStateSupplier() {
		return () -> new NimMultipleState(base);
	}

	@Override
	public Agent<NimAction> player(Scanner scan) {
		return new NimPlayer(scan);
	}

	@Override
	public QValueConverter converter() {
		return new WinLoseConverter();
	}

	@Override
	public InputConverter conv() {
		return new NimStateConverter();
	}

	@Override
	public int trainingEpisodes() {
		return 100000;
	}
}
