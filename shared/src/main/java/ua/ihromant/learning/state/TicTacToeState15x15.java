package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState15x15 implements TicTacToeState {
	private static final int HOR_SIZE = 15;
	private static final int VER_SIZE = 15;
	private static final int WON = 5;

	private final BitSet plrz;

	public TicTacToeState15x15() {
		this.plrz = new BitSet();
	}

	private TicTacToeState15x15(TicTacToeState15x15 prev, int nextMove) {
		this.plrz = (BitSet) prev.plrz.clone();
		if (isAssigned(nextMove)) {
			throw new IllegalArgumentException();
		}
		assign(nextMove, getCurrent());
	}

	public static TicTacToeState15x15 from(Player[] players) {
		if (players.length != HOR_SIZE * VER_SIZE) {
			throw new IllegalArgumentException("Please provide " + HOR_SIZE * VER_SIZE + " size of the array");
		}
		TicTacToeState15x15 state = new TicTacToeState15x15();
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				state.assign(i, players[i]);
			}
		}
		return state;
	}

	private boolean isAssigned(int position) {
		return plrz.get(position * 2);
	}

	private void assign(int position, Player pl) {
		if (plrz.get(position * 2)) {
			throw new IllegalStateException("Can't assign to already assigned field");
		}
		plrz.set(position * 2);
		if (pl == Player.X) {
			plrz.set(position * 2 + 1);
		}
	}

	@Override
	public Player getPlayer(int position) {
		if (!plrz.get(position * 2)) {
			return null;
		}
		return plrz.get(position * 2 + 1) ? Player.X : Player.O;
	}

	@Override
	public int winLength() {
		return WON;
	}

	@Override
	public int horSize() {
		return HOR_SIZE;
	}

	@Override
	public int verSize() {
		return VER_SIZE;
	}

	private BitSet andWithOther(BitSet other) {
		BitSet toTest = ((BitSet) plrz.clone());
		toTest.and(other);
		return toTest;
	}

	@Override
	public Player getCurrent() {
		return andWithOther(TERMINAL_MASK).cardinality() % 2 == 0 ? Player.X : Player.O;
	}

	@Override
	public int getMaximumMoves() {
		return HOR_SIZE * VER_SIZE - andWithOther(TERMINAL_MASK).cardinality();
	}

	@Override
	public Stream<TTTAction> getActions() {
		return IntStream.range(0, HOR_SIZE * VER_SIZE)
				.filter(i -> !isAssigned(i))
				.mapToObj(TTTAction::of);
	}

	@Override
	public State<TTTAction> apply(TTTAction action) {
		return new TicTacToeState15x15(this, action.getCoordinate());
	}

	private Player won() {
		for (int i = 0; i < WINNING_MASKS.length / 2; i++) {
			if (andWithOther(WINNING_MASKS[2 * i]).equals(WINNING_MASKS[2 * i + 1])) {
				return i % 2 == 0 ? Player.X : Player.O;
			}
		}
		return null;
	}

	@Override
	public Result getResult() {
		int moves = andWithOther(TERMINAL_MASK).cardinality();
		Player won = won();
		if (won != null) {
			return new BoardResult(won(), moves, 2 * WON - 1, 0.001);
		}
		if (moves == HOR_SIZE * VER_SIZE) {
			return new BoardResult();
		}
		return null;
	}

	@Override
	public GameResult getExpectedResult(Player pl) {
		return pl == Player.X ? GameResult.WIN : GameResult.LOSE;
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
		TicTacToeState15x15 that = (TicTacToeState15x15) o;
		return this.plrz.equals(that.plrz);
	}

	@Override
	public int hashCode() {
		return plrz.hashCode();
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

	private static final BitSet[] WINNING_MASKS = POSSIBLE_SHIFTS.entrySet().stream()
			.flatMap(e -> {
				BitSet[] result = new BitSet[e.getValue().length * 4];
				for (int i = 0; i < e.getValue().length; i++) {
					int start = e.getValue()[i];
					result[4 * i] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 1] = encodeLine(start, e.getKey(), Player.X);
					result[4 * i + 2] = encodeLine(start, e.getKey(), Player.X); // mask
					result[4 * i + 3] = encodeLine(start, e.getKey(), Player.O);
				}
				return Arrays.stream(result);
			}).toArray(BitSet[]::new);

	private static BitSet encodeLine(int start, int shift, Player player) {
		TicTacToeState15x15 state = new TicTacToeState15x15();
		for (int i = 0; i < WON; i++) {
			state.assign(start + i * shift, player);
		}
		return state.plrz;
	}

	private static final BitSet TERMINAL_MASK =
			IntStream.range(0, HOR_SIZE * VER_SIZE).boxed().collect(Collector.of(TicTacToeState15x15::new,
					(state, numb) -> state.assign(numb, Player.O),
					(state1, state2) -> {
						state1.plrz.or(state2.plrz);
						return state1;
					})).plrz;
}