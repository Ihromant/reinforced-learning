package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimLineState implements State<NimAction> {
	private static final int PILES_MAX = 4;
	private static final int BINARY_NUMBERS = 3;
	private final int[] piles;
	private final Player current;

	public NimLineState(int[] piles) {
		this(piles, Player.X);
	}

	private NimLineState(int[] piles, Player player) {
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
					return Arrays.stream(coeffsBigger)
							.mapToObj(coeff -> new NimAction(coeff, red));
				});
	}

	@Override
	public State<NimAction> apply(NimAction action) {
		return new NimLineState(take(this.piles, action.getCoeffs()[0], action.getReduce()),
						this.current == Player.X ? Player.O : Player.X);
	}

	private static int[] take(int[] from, int idx, int reduce) {
		int[] result = Arrays.copyOf(from, from.length);
		result[idx] = result[idx] - reduce;
		return result;
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
				result[i * BINARY_NUMBERS + BINARY_NUMBERS - 1 - j] = binary[binary.length  - 1 - j] - '0';
			}
		}
		return result;
	}

	@Override
	public int getMaximumMoves() {
		return Arrays.stream(piles).sum();
	}

	@Override
	public GameResult getExpectedResult(Player pl) {
		return Arrays.stream(piles).reduce((a, b) -> a ^ b).orElse(0) == 0 ^ pl == Player.X ? GameResult.WIN : GameResult.LOSE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NimLineState nimState = (NimLineState) o;
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