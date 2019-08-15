package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimState implements State<NimAction> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;
	private final int[] piles;
	private final Player current;

	public NimState(int[] piles) {
		this(piles, Player.X);
	}

	private NimState(int[] piles, Player player) {
		this.piles = Arrays.stream(piles).sorted().filter(i -> i != 0).toArray();
		this.current = player;
	}

	@Override
	public Stream<NimAction> getActs() {
		if (isTerminal()) {
			return Stream.empty();
		}

		return IntStream.rangeClosed(1, Arrays.stream(piles).max().orElse(0))
				.boxed()
				.flatMap(red -> {
					int[] coeffsBigger = IntStream.range(0, piles.length)
							.filter(i -> piles[i] >= red).toArray();
					return powerSetNotEpmty(coeffsBigger).map(coeffs -> new NimAction(coeffs, red));
				});
	}

	@Override
	public State<NimAction> apply(NimAction action) {
		return new NimState(take(this.piles, action.getCoeffs(), action.getReduce()),
						this.current == Player.X ? Player.O : Player.X);
	}

	private static int[] take(int[] from, int[] indices, int reduce) {
		int[] result = Arrays.copyOf(from, from.length);
		for (int i : indices) {
			result[i] = result[i] - reduce;
		}
		return result;
	}

	private static Stream<int[]> powerSetNotEpmty(int[] base) {
		return IntStream.range(1, 1 << base.length)
				.mapToObj(i -> {
					int[] res = new int[Integer.bitCount(i)];
					int counter = 0;
					int idx = 0;
					while (i != 0) {
						if (i % 2 == 1) {
							res[idx++] = base[counter];
						}
						counter++;
						i = i / 2;
					}
					return res;
				});
	}

	@Override
	public boolean isTerminal() {
		return Arrays.stream(piles).allMatch(i -> i == 0);
	}

	@Override
	public GameResult getUtility(Player player) {
		if (!isTerminal()) {
			return GameResult.DRAW;
		}

		return this.current == player ? GameResult.WIN : GameResult.LOSE;
	}

	@Override
	public Player getCurrent() {
		return current;
	}

	@Override
	public double[] toModel() {
		double[] result = new double[PILES_MAX * BINARY_NUMBERS + 1];
		result[result.length - 1] = getCurrent() == Player.X ? 1 : 0;
		for (int i = 0; i < PILES_MAX && i < piles.length; i++) {
			char[] binary = Integer.toBinaryString(piles[i]).toCharArray();
			for (int j = 0; j < binary.length && j < BINARY_NUMBERS; j++) {
				result[i * BINARY_NUMBERS + BINARY_NUMBERS - 1 - j] = binary[binary.length - 1 - j] - '0';
			}
		}
		return result;
	}

	@Override
	public int getMaximumMoves() {
		return Arrays.stream(piles).sum();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NimState nimState = (NimState) o;
		return Arrays.equals(piles, nimState.piles) &&
				current == nimState.current;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(current);
		result = 31 * result + Arrays.hashCode(piles);
		return result;
	}

	@Override
	public String toString() {
		return current + ":" + Arrays.toString(piles);
	}
}
