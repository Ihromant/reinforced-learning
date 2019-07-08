package ua.ihromant.learning;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeStateSized implements State {
	private static final int SIZE = 5;
	private static final int WON = 4;
	private final Player[] players = new Player[25];

	public TicTacToeStateSized() {
	}

	private TicTacToeStateSized(TicTacToeStateSized prev) {
		System.arraycopy(prev.players, 0, this.players, 0, players.length);
	}

	public TicTacToeStateSized(TicTacToeStateSized prev, int nextMove, Player pl) {
		this(prev);
		if (players[nextMove] != null) {
			throw new IllegalArgumentException();
		}
		players[nextMove] = pl;
	}

	@Override
	public Player getCurrent() {
		return IntStream.range(0, SIZE * SIZE)
				.filter(i -> players[i] == null)
				.count() % 2 == 1 ? Player.X : Player.O;
	}

	private static final int[] noTop = {0, 1};
	private static final int[] noBot = {-1, 0};
	private static final int[] all = {-1, 0, 1};

	private IntStream neighbors(int position) {
		int[] hor;
		int[] ver;
		if (position / SIZE == 0) {
			hor = noTop;
		} else if (position / SIZE == SIZE - 1) {
			hor = noBot;
		} else {
			hor = all;
		}
		if (position % SIZE == 0) {
			ver = noTop;
		} else if (position % SIZE == SIZE - 1) {
			ver = noBot;
		} else {
			ver = all;
		}
		return Arrays.stream(hor).flatMap(i -> Arrays.stream(ver).map(j -> position + i * 5 + j));
	}

	@Override
	public Stream<Action> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		Set<Integer> assigned = IntStream.range(0, SIZE * SIZE)
				.filter(i -> players[i] != null)
				.boxed()
				.collect(Collectors.toSet());

		if (assigned.isEmpty()) {
			return Stream.of(new Action(Player.X, this, new TicTacToeStateSized(this, 12, Player.X)));
		}

		return assigned.stream()
				.mapToInt(Integer::intValue)
				.flatMap(this::neighbors)
				.filter(i -> !assigned.contains(i))
				.mapToObj(i -> {
					TicTacToeStateSized next = new TicTacToeStateSized(this);
					Player player = assigned.size() % 2 == 0 ? Player.X : Player.O;
					next.players[i] = player;
					return new Action(player, this, next);
				});
	}

	private Player won() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0 ;j < SIZE; j++) {
				Player pl = players[i * SIZE + j];
				if (pl == null) {
					continue;
				}

				if (i <= SIZE - WON && j <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != players[(i + k) * SIZE + (j + k)]) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}

				if (j <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != players[i * SIZE + (j + k)]) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}

				if (i <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != players[(i + k) * SIZE + j]) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}
				if (i <= SIZE - WON && j >= WON - 1) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != players[(i + k) * SIZE + (j - k)]) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isTerminal() {
		return Arrays.stream(players).filter(Objects::nonNull).count() == SIZE * SIZE || won() != null;
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
				if (players[coef] != null) {
					builder.append(players[coef].toString());
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
		return Arrays.equals(players, that.players);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(players);
	}
}