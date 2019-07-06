package ua.ihromant.learning;

import java.util.Objects;

public class Action {
	private final Player player;
	private final State from;
	private final State to;

	public Action(Player player, State from, State to) {
		this.player = player;
		this.from = from;
		this.to = to;
	}

	public Player getPlayer() {
		return player;
	}

	public State getFrom() {
		return from;
	}

	public State getTo() {
		return to;
	}

	public double getReward() {
		return to.getUtility(player);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Action action = (Action) o;
		return player == action.player &&
				Objects.equals(from, action.from) &&
				Objects.equals(to, action.to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(player, from, to);
	}

	@Override
	public String toString() {
		return "Action{" +
				"player=" + player +
				", from=" + from +
				", to=" + to +
				'}';
	}
}
