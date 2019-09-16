package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimMultipleState implements NimState {
	private final int[] piles;
	private final Player current;

	public NimMultipleState(int[] piles) {
		this(piles, Player.X);
	}

	private NimMultipleState(int[] piles, Player player) {
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
					return powerSetNotEpmty(coeffsBigger).map(coeffs -> new NimAction(coeffs, red));
				});
	}

	@Override
	public State<NimAction> apply(NimAction action) {
		return new NimMultipleState(take(this.piles, action.getCoeffs(), action.getReduce()),
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NimMultipleState nimMultipleState = (NimMultipleState) o;
		return Arrays.equals(piles, nimMultipleState.piles) &&
				current == nimMultipleState.current;
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
