package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState5x5 implements TicTacToeState {
	private static final int SIZE = 5;
	private static final int WON = 4;
	private long plrz;

	public TicTacToeState5x5() {
	}

	private TicTacToeState5x5(TicTacToeState5x5 prev) {
		this.plrz = prev.plrz;
	}

	private TicTacToeState5x5(TicTacToeState5x5 prev, int nextMove) {
		this(prev);
		if (isAssigned(nextMove)) {
			throw new IllegalArgumentException();
		}
		assign(nextMove, getCurrent());
	}

	public static TicTacToeState5x5 from(Player[] players) {
		if (players.length != SIZE * SIZE) {
			throw new IllegalArgumentException("Please provide " + SIZE * SIZE + " size of the array");
		}
		TicTacToeState5x5 state = new TicTacToeState5x5();
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

	@Override
	public Player getPlayer(int position) {
		if (!get(position * 2)) {
			return null;
		}
		return get(position * 2 + 1) ? Player.X : Player.O;
	}

	@Override
	public Player getCurrent() {
		return Long.bitCount(plrz & TERMINAL_MASK) % 2 == 0 ? Player.X : Player.O;
	}

	@Override
	public int getMaximumMoves() {
		return SIZE * SIZE - Long.bitCount(plrz & TERMINAL_MASK);
	}

	@Override
	public Stream<TTTAction> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		return IntStream.range(0, SIZE * SIZE)
				.filter(i -> !isAssigned(i))
				.mapToObj(TTTAction::new);
	}

	@Override
	public State<TTTAction> apply(TTTAction action) {
		return new TicTacToeState5x5(this, action.getCoordinate());
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
	public GameResult getUtility(Player player) {
		Player won = won();
		if (won == null) {
			return GameResult.DRAW;
		}

		return player == won ? GameResult.WIN : GameResult.LOSE;
	}

	@Override
	public GameResult getExpectedResult(Player pl) {
		return GameResult.DRAW;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("-".repeat(SIZE + 2));
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
		builder.append("-".repeat(SIZE + 2));
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
		TicTacToeState5x5 that = (TicTacToeState5x5) o;
		return this.plrz == that.plrz;
	}

	@Override
	public int hashCode() {
		long res = 0;
		for (int i = 0; i < SIZE * SIZE; i++) {
			long rest = (plrz >>> (2 * i)) % 4;
			rest = rest == 3 ? 2 : rest;
			res = res * 3 + rest;
		}
		return Long.hashCode(res);
	}

	// ABCDE
	// FGHIJ
	// KLMNO
	// PQRST
	// UVWXY
	private static final Map<Integer, char[]> POSSIBLE_SHIFTS = new HashMap<>() {
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
		TicTacToeState5x5 state = new TicTacToeState5x5();
		for (int i = 0; i < WON; i++) {
			state.assign(start + i * shift, player);
		}
		return state.plrz;
	}

	private static final long TERMINAL_MASK =
			IntStream.range(0, SIZE * SIZE).boxed().collect(Collector.of(TicTacToeState5x5::new,
					(state, numb) -> state.assign(numb, Player.O),
					(state1, state2) -> {
						TicTacToeState5x5 newState = new TicTacToeState5x5(state1);
						newState.plrz |= state2.plrz;
						return newState;
					})).plrz;
}