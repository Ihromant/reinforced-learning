package ua.ihromant.learning.ai.converter;

import java.util.stream.IntStream;

import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;

public class TicTacToeStateConverter implements InputConverter<TTTAction> {
	private final int size;
	public TicTacToeStateConverter(int size) {
		this.size = size;
	}

	@Override
	public double[] convert(StateAction<TTTAction> stateAction) {
		TicTacToeState state = stateAction.getResult();
		double[] res = new double[inputLength()];
		IntStream.range(0, size)
				.forEach(i -> {
					Player pl = state.getPlayer(i);
					if (pl == Player.X) {
						res[i] = 1;
					}
					if (pl == Player.O) {
						res[i + size] = 1;
					}
					if (pl == null) {
						res[i + 2 * size] = 1;
					}
				});
		res[res.length - 1] = state.getCurrent().ordinal();
		return res;
	}

	@Override
	public StateAction<TTTAction> reverse(double[] from) {
		return null;
	}

	@Override
	public int inputLength() {
		return size * 3 + 1;
	}
}
