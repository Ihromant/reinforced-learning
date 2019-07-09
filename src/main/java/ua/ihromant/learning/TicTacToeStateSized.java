package ua.ihromant.learning;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeStateSized implements State {
	private static final int SIZE = 5;
	private static final int WON = 4;
	private final BitSet players = new BitSet(2 * SIZE * SIZE);

	public TicTacToeStateSized() {
	}

	private TicTacToeStateSized(TicTacToeStateSized prev) {
		players.or(prev.players);
	}

	private boolean isAssigned(int position) {
		return players.get(position * 2);
	}

	private void assign(int position, Player pl) {
		if (players.get(position * 2)) {
			throw new IllegalStateException("Can't assign to already assigned field");
		}
		players.set(position * 2);
		players.set(position * 2 + 1, pl == Player.X);
	}

	private Player getPlayer(int position) {
		if (!players.get(position * 2)) {
			return null;
		}
		return players.get(position * 2 + 1) ? Player.X : Player.O;
	}

	public TicTacToeStateSized(TicTacToeStateSized prev, int nextMove, Player pl) {
		this(prev);
		if (isAssigned(nextMove)) {
			throw new IllegalArgumentException();
		}
		assign(nextMove, pl);
	}

	@Override
	public Player getCurrent() {
		return IntStream.range(0, SIZE * SIZE)
				.filter(i -> !isAssigned(i))
				.count() % 2 == 1 ? Player.X : Player.O;
	}

	@Override
	public Stream<Action> getActions() {
		if (isTerminal()) {
			return Stream.empty();
		}

		List<Integer> notAssigned = IntStream.range(0, SIZE * SIZE)
				.filter(i -> !isAssigned(i))
				.boxed()
				.collect(Collectors.toList());
		Collections.shuffle(notAssigned);
		return notAssigned.stream().map(i -> {
			TicTacToeStateSized next = new TicTacToeStateSized(this);
			Player player = notAssigned.size() % 2 == 1 ? Player.X : Player.O;
			next.assign(i, player);
			return new Action(player, this, next);
		});
	}

	private Player won() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0 ;j < SIZE; j++) {
				Player pl = getPlayer(i * SIZE + j);
				if (pl == null) {
					continue;
				}

				if (i <= SIZE - WON && j <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != getPlayer((i + k) * SIZE + (j + k))) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}

				if (j <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != getPlayer(i * SIZE + (j + k))) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}

				if (i <= SIZE - WON) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != getPlayer((i + k) * SIZE + j)) {
							line = false;
							break;
						}
					}
					if (line) return pl;
				}
				if (i <= SIZE - WON && j >= WON - 1) {
					boolean line = true;
					for (int k = 0; k < WON; k++) {
						if (pl != getPlayer((i + k) * SIZE + (j - k))) {
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
		return IntStream.range(0, SIZE * SIZE).allMatch(this::isAssigned) || won() != null;
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
		return players.equals(that.players);
	}

	@Override
	public int hashCode() {
		return Objects.hash(players);
	}
}