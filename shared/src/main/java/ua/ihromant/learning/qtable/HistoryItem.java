package ua.ihromant.learning.qtable;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.Result;
import ua.ihromant.learning.state.State;

public class HistoryItem<A> {
    private final State<A> from;
    private final A action;
    private final State<A> to;
    private final Player player;
    private final boolean random;

    public HistoryItem(State<A> from, A action, State<A> to, Player player, boolean random) {
        this.from = from;
        this.action = action;
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

    public StateAction<A> getStateAction() {
    	return new StateAction<>(from, action);
    }

    public Result getResult() {
        return to.getResult();
    }
}
