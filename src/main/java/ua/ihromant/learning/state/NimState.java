package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimState implements State {
	private final int[] piles;
	private final Player current;

	public NimState(int[] piles) {
		this(piles, Player.X);
	}

	private NimState(int[] piles, Player player) {
		this.piles = Arrays.stream(piles).sorted().filter(i -> i != 0).toArray();
		this.current = player;
	}

	public NimState(NimState state, int[] coeffs, int red) {
		this(take(state.piles, coeffs, red),
				state.current == Player.X ? Player.O : Player.X);
	}

	@Override
	public Stream<Action> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		Player next = current == Player.X ? Player.O: Player.X;

		return IntStream.rangeClosed(1, Arrays.stream(piles).max().orElse(0))
				.boxed()
				.flatMap(red -> {
					int[] coeffsBigger = IntStream.range(0, piles.length)
							.filter(i -> piles[i] >= red).toArray();
					return powerSetNotEpmty(coeffsBigger).map(coeffs -> {
						int[] newPiles = take(piles, coeffs, red);
						NimState newState = new NimState(newPiles, next);
						return new Action(current, this, newState);
					});
				});
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
	public double getUtility(Player player) {
		if (!isTerminal()) {
			return 0;
		}

		return this.current == player ? 1 : -1;
	}

	@Override
	public Player getCurrent() {
		return current;
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
		return "NimState{" +
				"piles=" + Arrays.toString(piles) +
				", current=" + current +
				'}';
	}
}
