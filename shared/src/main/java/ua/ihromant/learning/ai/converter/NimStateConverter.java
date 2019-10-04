package ua.ihromant.learning.ai.converter;

import java.util.Arrays;

import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimState;
import ua.ihromant.learning.state.Player;

public class NimStateConverter implements InputConverter<NimAction> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;

	@Override
	public double[] convert(StateAction<NimAction> stateAction) {
		NimState state = stateAction.getState();
		int[] piles = state.getPiles();
		NimAction action = stateAction.getAction();
		Arrays.stream(action.getCoeffs()).filter(i -> piles[i] < action.getReduce())
				.findAny().ifPresent(i -> {
			throw new IllegalArgumentException(String.valueOf(stateAction));
		});
		double[] result = new double[inputLength()];
		result[result.length - 1] = state.getCurrent() == Player.X ? 1 : 0;
		for (int i = 0; i < PILES_MAX && i < piles.length; i++) {
			char[] binary = Integer.toBinaryString(piles[i]).toCharArray();
			for (int j = 0; j < binary.length && j < BINARY_NUMBERS; j++) {
				result[i * BINARY_NUMBERS + BINARY_NUMBERS - 1 - j] = binary[binary.length - 1 - j] - '0';
			}
		}
		for (int i : action.getCoeffs()) {
			result[PILES_MAX * BINARY_NUMBERS + i] = 1;
		}
		char[] binary = Integer.toBinaryString(action.getReduce()).toCharArray();
		for (int i = 0; i < binary.length; i++) {
			result[PILES_MAX * BINARY_NUMBERS + PILES_MAX + BINARY_NUMBERS - 1 - i] =
					binary[binary.length - 1 - i] - '0';
		}
		return result;
	}

	@Override
	public int inputLength() {
		return PILES_MAX * BINARY_NUMBERS + BINARY_NUMBERS + PILES_MAX + 1;
	}
}
