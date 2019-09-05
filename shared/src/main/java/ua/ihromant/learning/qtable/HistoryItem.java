package ua.ihromant.learning.qtable;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class HistoryItem<A> {
    private final State<A> from;
    private final State<A> to;
    private final Player player;
    private final boolean random;

    public HistoryItem(State<A> from, State<A> to, Player player, boolean random) {
        this.from = from;
        this.to = to;
        this.player = player;
        this.random = random;
    }

    public State<A> getFrom() {
        return from;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRandom() {
        return random;
    }

    public State<A> getTo() {
        return to;
    }
}
