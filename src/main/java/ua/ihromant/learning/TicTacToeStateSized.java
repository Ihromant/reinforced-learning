package ua.ihromant.learning;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeStateSized implements State {
	private static final int SIZE = 5;
	private static final int WON = 4;
	private long plrz;

	public TicTacToeStateSized() {
	}

	private TicTacToeStateSized(TicTacToeStateSized prev) {
		this.plrz = prev.plrz;
	}

	public TicTacToeStateSized(TicTacToeStateSized prev, int nextMove, Player pl) {
		this(prev);
		if (isAssigned(nextMove)) {
			throw new IllegalArgumentException();
		}
		assign(nextMove, pl);
	}

	public static TicTacToeStateSized from(Player[] players) {
		if (players.length != SIZE * SIZE) {
			throw new IllegalArgumentException("Please provide " + SIZE * SIZE + " size of the array");
		}
		TicTacToeStateSized state = new TicTacToeStateSized();
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				state.assign(i, players[i]);
			}
		}
		return state;
	}

	private boolean get(int index) {
		return (plrz & (1L << index)) != 0;
	}

	private void set(int index) {
		plrz |= (1L << index);
	}

	private boolean isAssigned(int position) {
		return get(position * 2);
	}

	private void assign(int position, Player pl) {
		if (get(position * 2)) {
			throw new IllegalStateException("Can't assign to already assigned field");
		}
		set(position * 2);
		if (pl == Player.X) {
			set(position * 2 + 1);
		}
	}

	private Player getPlayer(int position) {
		if (!get(position * 2)) {
			return null;
		}
		return get(position * 2 + 1) ? Player.X : Player.O;
	}

	@Override
	public Player getCurrent() {
		return IntStream.range(0, SIZE * SIZE)
				.filter(this::isAssigned)
				.count() % 2 == 0 ? Player.X : Player.O;
	}

	@Override
	public Stream<Action> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		if (plrz == 0) {
			return Stream.of(new Action(Player.X, this, FIRST_MOVE));
		}

		int assignedSize = Long.bitCount(plrz & TERMINAL_MASK);
		return IntStream.range(0, SIZE * SIZE)
				.filter(i -> !isAssigned(i))
				.mapToObj(i -> new Action(
						assignedSize % 2 == 0 ? Player.X : Player.O,
						this,
						new TicTacToeStateSized(this, i,
								assignedSize % 2 == 0 ?	Player.X : Player.O)));
	}

	private Player won() {
		for (int i = 0; i < WINNING_MASKS.length / 2; i++) {
			if ((plrz & WINNING_MASKS[2 * i]) == WINNING_MASKS[2 * i + 1]) {
				return i % 2 == 0 ? Player.X : Player.O;
			}
		}
		return null;
	}

	@Override
	public boolean isTerminal() {
		return (plrz & TERMINAL_MASK) == TERMINAL_MASK || won() != null;
	}

	@Override
	public double getUtility(Player player) {
		Player won = won();
		if (won == null) {
			return 0;
		}

		return player == won ? 1 : -1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < SIZE + 2; i++) {
			builder.append('-');
		}
		builder.append('\n');
		for (int i = 0; i < SIZE; i++) {
			builder.append('|');
			for (int j = 0; j < SIZE; j++) {
				int coef = i * SIZE + j;
				if (isAssigned(coef)) {
					builder.append(getPlayer(coef).toString());
				} else {
					builder.append(' ');
				}
			}
			builder.append('|');
			builder.append('\n');
		}
		for (int i = 0; i < SIZE + 2; i++) {
			builder.append('-');
		}
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TicTacToeStateSized that = (TicTacToeStateSized) o;
		return this.plrz == that.plrz;
	}

	@Override
	public int hashCode() {
		return (int) plrz;
	}

	// ABCDE
	// FGHIJ
	// KLMNO
	// PQRST
	// UVWXY
	private static final Map<Integer, char[]> POSSIBLE_SHIFTS = new HashMap<Integer, char[]>() {
		{
			put(1, "ABFGKLPQUV".toCharArray()); // 1
			put(SIZE, "ABCDEFGHIJ".toCharArray()); // 5
			put(SIZE + 1, "ABFG".toCharArray()); // 6
			put(SIZE - 1, "DEIJ".toCharArray()); // 4
		}
	};

	private static final long[] WINNING_MASKS = POSSIBLE_SHIFTS.entrySet().stream()
			.flatMapToLong(e -> {
				long[] result = new long[e.getValue().length * 4];
				for (int i = 0; i < e.getValue().length; i++) {
					int start = e.getValue()[i] - 'A';
					result[4 * i] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 1] = encodeLine(start, e.getKey(), Player.X);
					result[4 * i + 2] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 3] = encodeLine(start, e.getKey(), Player.O);
				}
				return Arrays.stream(result);
			}).toArray();

	private static long encodeLine(int start, int shift, Player player) {
		TicTacToeStateSized state = new TicTacToeStateSized();
		for (int i = 0; i < WON; i++) {
			state.assign(start + i * shift, player);
		}
		return state.plrz;
	}

	private static final TicTacToeStateSized FIRST_MOVE = new TicTacToeStateSized(new TicTacToeStateSized(), 12, Player.X);

	private static final long TERMINAL_MASK =
			IntStream.range(0, 25).boxed().collect(Collector.of(TicTacToeStateSized::new,
					(state, numb) -> state.assign(numb, Player.O),
					(state1, state2) -> {
						TicTacToeStateSized newState = new TicTacToeStateSized(state1);
						newState.plrz |= state2.plrz;
						return newState;
					})).plrz;
}