package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimLineState implements NimState {
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
	public Stream<NimAction> getActions() {
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
	public Result getResult() {
		if (Arrays.stream(piles).allMatch(i -> i == 0)) {
			return new BoardResult(this.current);
		}
		return null;
	}

	@Override
	public Player getCurrent() {
		return current;
	}

	@Override
	public int getMaximumMoves() {
		return Arrays.stream(piles).sum();
	}

	@Override
	public int[] getPiles() {
		return piles;
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