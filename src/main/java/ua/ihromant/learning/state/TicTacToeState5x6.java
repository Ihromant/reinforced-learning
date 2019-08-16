package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState5x6 implements ITicTacToeState {
	private static final int HOR_SIZE = 5;
	private static final int VER_SIZE = 6;
	private static final int WON = 4;
	private long plrz;

	public TicTacToeState5x6() {
	}

	private TicTacToeState5x6(TicTacToeState5x6 prev) {
		this.plrz = prev.plrz;
	}

	private TicTacToeState5x6(TicTacToeState5x6 prev, int nextMove) {
		this(prev);
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
	public Player getCurrent() {
		return Long.bitCount(plrz & TERMINAL_MASK) % 2 == 0 ? Player.X : Player.O;
	}

	@Override
	public double[] toModel() {
		double[] result = new double[3 * HOR_SIZE * VER_SIZE];
		IntStream.range(0, HOR_SIZE * VER_SIZE)
				.forEach(i -> {
					Player pl = getPlayer(i);
					if (pl == Player.X) {
						result[i] = 1;
					}
					if (pl == Player.O) {
						result[i + HOR_SIZE * VER_SIZE] = 1;
					}
					if (pl == null) {
						result[i + HOR_SIZE * VER_SIZE * 2] = 1;
					}
				});
		return result;
	}

	@Override
	public int getMaximumMoves() {
		return HOR_SIZE * VER_SIZE - Long.bitCount(plrz & TERMINAL_MASK);
	}

	@Override
	public Stream<TTTAction> getActs() {
		if (isTerminal()) {
			return Stream.empty();
		}

		if (plrz == 0) {
			return Stream.of(new TTTAction(15));
		}

		return IntStream.range(0, HOR_SIZE * VER_SIZE)
				.filter(i -> !isAssigned(i))
				.mapToObj(TTTAction::new);
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
		return pl == Player.X ? GameResult.WIN : GameResult.LOSE;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < VER_SIZE + 2; i++) {
			builder.append('-');
		}
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
		for (int i = 0; i < VER_SIZE + 2; i++) {
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

	private static final Map<Integer, int[]> POSSIBLE_SHIFTS = new HashMap<Integer, int[]>() {
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
						TicTacToeState5x6 newState = new TicTacToeState5x6(state1);
						newState.plrz |= state2.plrz;
						return newState;
					})).plrz;
}