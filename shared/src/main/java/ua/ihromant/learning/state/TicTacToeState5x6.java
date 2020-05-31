package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState5x6 implements TicTacToeState {
	private static final int HOR_SIZE = 5;
	private static final int VER_SIZE = 6;
	private static final int WON = 4;
	private static final Map<TicTacToeState5x6, GameResult> EXPECTED = new HashMap<>() {
		{
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 8), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 9), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 14), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 15), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 20), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 21), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 7), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 10), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 13), GameResult.DRAW);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 16), GameResult.DRAW);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 19), GameResult.WIN);
			put(new TicTacToeState5x6(new TicTacToeState5x6(), 22), GameResult.WIN);
		}
	};
	private long plrz;

	public TicTacToeState5x6() {
	}

	private TicTacToeState5x6(TicTacToeState5x6 prev, int nextMove) {
		this.plrz = prev.plrz;
		if (isAssigned(nextMove)) {
			throw new IllegalArgumentException();
		}
		assign(nextMove, getCurrent());
	}

	public static TicTacToeState5x6 from(Player[] players) {
		if (players.length != HOR_SIZE * VER_SIZE) {
			throw new IllegalArgumentException("Please provide " + HOR_SIZE * VER_SIZE + " size of the array");
		}
		TicTacToeState5x6 state = new TicTacToeState5x6();
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
	public int horSize() {
		return HOR_SIZE;
	}

	@Override
	public int verSize() {
		return VER_SIZE;
	}

	@Override
	public Player getCurrent() {
		return Long.bitCount(plrz & TERMINAL_MASK) % 2 == 0 ? Player.X : Player.O;
	}

	@Override
	public int getMaximumMoves() {
		return HOR_SIZE * VER_SIZE - Long.bitCount(plrz & TERMINAL_MASK);
	}

	@Override
	public Stream<TTTAction> getActions() {
		return IntStream.range(0, HOR_SIZE * VER_SIZE)
				.filter(i -> !isAssigned(i))
				.mapToObj(TTTAction::of);
	}

	@Override
	public State<TTTAction> apply(TTTAction action) {
		return new TicTacToeState5x6(this, action.getCoordinate());
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
	public Result getResult() {
		int moves = Long.bitCount(plrz & TERMINAL_MASK);
		Player won = won();
		if (won != null) {
			return new BoardResult(won(), moves, 2 * WON - 1);
		}
		if (moves == HOR_SIZE * VER_SIZE) {
			return new BoardResult();
		}
		return null;
	}

	@Override
	public GameResult getExpectedResult(Player pl) {
		if (this.plrz == 0) {
			return pl == Player.X ? GameResult.WIN : GameResult.LOSE;
		}
		return EXPECTED.getOrDefault(this, GameResult.LOSE);  // TODO fix for Player.O
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("-".repeat(VER_SIZE + 2));
		builder.append('\n');
		for (int i = 0; i < HOR_SIZE; i++) {
			builder.append('|');
			for (int j = 0; j < VER_SIZE; j++) {
				int coef = i * VER_SIZE + j;
				if (isAssigned(coef)) {
					builder.append(getPlayer(coef).toString());
				} else {
					builder.append(' ');
				}
			}
			builder.append('|');
			builder.append('\n');
		}
		builder.append("-".repeat(VER_SIZE + 2));
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
		TicTacToeState5x6 that = (TicTacToeState5x6) o;
		return this.plrz == that.plrz;
	}

	@Override
	public int hashCode() {
		long res = 0;
		for (int i = 0; i < HOR_SIZE * VER_SIZE; i++) {
			long rest = (plrz >>> (2 * i)) % 4;
			rest = rest == 3 ? 2 : rest;
			res = res * 3 + rest;
		}
		return Long.hashCode(res);
	}

	private static final Map<Integer, int[]> POSSIBLE_SHIFTS = new HashMap<>() {
		{
			put(1, getStartingPositionsForDirection(1, 0)); // 1
			put(VER_SIZE, getStartingPositionsForDirection(0, 1)); // 5
			put(VER_SIZE + 1, getStartingPositionsForDirection(1, 1)); // 6
			put(VER_SIZE - 1, getStartingPositionsForDirection(-1, 1)); // 4
		}
	};

	private static int[] getStartingPositionsForDirection(int horShift, int verShift) {
		return IntStream.range(0, HOR_SIZE * VER_SIZE)
				.filter(i -> {
					int verPos = i / VER_SIZE;
					int horPos = i % VER_SIZE;
					return verPos + verShift * (WON - 1) < HOR_SIZE
							&& horPos + horShift * (WON - 1) < VER_SIZE
							&& horPos + horShift * (WON - 1) >= 0;
				})
				.toArray();
	}

	private static final long[] WINNING_MASKS = POSSIBLE_SHIFTS.entrySet().stream()
			.flatMapToLong(e -> {
				long[] result = new long[e.getValue().length * 4];
				for (int i = 0; i < e.getValue().length; i++) {
					int start = e.getValue()[i];
					result[4 * i] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 1] = encodeLine(start, e.getKey(), Player.X);
					result[4 * i + 2] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 3] = encodeLine(start, e.getKey(), Player.O);
				}
				return Arrays.stream(result);
			}).toArray();

	private static long encodeLine(int start, int shift, Player player) {
		TicTacToeState5x6 state = new TicTacToeState5x6();
		for (int i = 0; i < WON; i++) {
			state.assign(start + i * shift, player);
		}
		return state.plrz;
	}

	private static final long TERMINAL_MASK =
			IntStream.range(0, HOR_SIZE * VER_SIZE).boxed().collect(Collector.of(TicTacToeState5x6::new,
					(state, numb) -> state.assign(numb, Player.O),
					(state1, state2) -> {
						state1.plrz |= state2.plrz;
						return state1;
					})).plrz;
}