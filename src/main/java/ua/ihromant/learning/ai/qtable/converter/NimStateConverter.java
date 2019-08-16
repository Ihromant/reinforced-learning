package ua.ihromant.learning.ai.qtable.converter;

import ua.ihromant.learning.state.INimState;
import ua.ihromant.learning.state.Player;

public class NimStateConverter implements InputConverter<INimState> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;

	@Override
	public double[] convert(INimState state) {
		double[] result = new double[inputLength()];
		result[result.length - 1] = state.getCurrent() == Player.X ? 1 : 0;
		int[] piles = state.getPiles();
		for (int i = 0; i < PILES_MAX && i < piles.length; i++) {
			char[] binary = Integer.toBinaryString(piles[i]).toCharArray();
			for (int j = 0; j < binary.length && j < BINARY_NUMBERS; j++) {
				result[i * BINARY_NUMBERS + BINARY_NUMBERS - 1 - j] = binary[binary.length - 1 - j] - '0';
			}
		}
		return result;
	}

	@Override
	public int inputLength() {
		return PILES_MAX * BINARY_NUMBERS + 1;
	}
}
