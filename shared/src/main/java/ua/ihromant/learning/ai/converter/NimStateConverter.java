package ua.ihromant.learning.ai.converter;

import java.util.Arrays;
import java.util.stream.IntStream;

import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimLineState;
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
	public StateAction<NimAction> reverse(double[] from) {
		int[] piles = new int[PILES_MAX];
		for (int i = 0; i < PILES_MAX; i++) {
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < BINARY_NUMBERS; j++) {
				double val = from[i * BINARY_NUMBERS + j];
				if (builder.length() > 0 || val > 0) {
					builder.append(val > 0 ? '1' : '0');
				}
			}
			piles[i] = Integer.parseInt(builder.length() > 0 ? builder.toString() : "0", 2);
		}
		NimState state = new NimLineState(piles, Player.X);
		StringBuilder builder = new StringBuilder();
		for (int i = PILES_MAX * BINARY_NUMBERS + PILES_MAX; i < from.length; i++) {
			double val = from[i];
			if (builder.length() > 0 || val > 0) {
				builder.append(val > 0 ? '1' : '0');
			}
		}
		NimAction act = new NimAction(
				IntStream.range(PILES_MAX * BINARY_NUMBERS, PILES_MAX * BINARY_NUMBERS + PILES_MAX)
				.filter(i -> from[i] > 0).map(i -> i - PILES_MAX * BINARY_NUMBERS).findFirst().orElseThrow(IllegalStateException::new),
				Integer.parseInt(builder.toString(), 2));
		return new StateAction<>(state, act);
	}

	@Override
	public int inputLength() {
		return PILES_MAX * BINARY_NUMBERS + BINARY_NUMBERS + PILES_MAX;
	}
}
