package ua.ihromant.learning.ai.converter;

import java.util.stream.IntStream;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TicTacToeState;

public class TicTacToeStateConverter implements InputConverter<TicTacToeState> {
	private int size;
	public TicTacToeStateConverter(int size) {
		this.size = size;
	}

	@Override
	public double[] convert(TicTacToeState state) {
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
		return res;
	}

	@Override
	public int inputLength() {
		return size * 3;
	}
}
