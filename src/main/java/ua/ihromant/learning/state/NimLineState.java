package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NimLineState implements State {
	private final int[] piles;
	private final Player current;

	public NimLineState(int[] piles) {
		this(piles, Player.X);
	}

	private NimLineState(int[] piles, Player player) {
		this.piles = Arrays.stream(piles).sorted().filter(i -> i != 0).toArray();
		this.current = player;
	}

	public NimLineState(NimLineState state, int coeff, int red) {
		this(take(state.piles, coeff, red),
				state.current == Player.X ? Player.O : Player.X);
	}

	@Override
	public Stream<Action> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		return IntStream.rangeClosed(1, Arrays.stream(piles).max().orElse(0))
				.boxed()
				.flatMap(red -> {
					int[] coeffsBigger = IntStream.range(0, piles.length)
							.filter(i -> piles[i] >= red).toArray();
					return Arrays.stream(coeffsBigger)
							.mapToObj(coeff -> new Action(current, this, new NimLineState(this, coeff, red)));
				});
	}

	@Override
	public State apply(Object o) {
		return null;
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
	public double[] toModel() {
		double[] result = new double[10];
		result[0] = getCurrent() == Player.X ? 1 : -1;
		for (int i = 0; i < piles.length; i++) {
			result[i + 1] = piles[i];
		}
		return result;
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
		return "NimState{" +
				"piles=" + Arrays.toString(piles) +
				", current=" + current +
				'}';
	}
}