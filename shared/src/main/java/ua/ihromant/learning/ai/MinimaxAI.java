package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.util.MaxUtil;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MinimaxAI<A> implements Agent<A> {
    private final Player player;
    public MinimaxAI(Player player) {
        this.player = player;
    }

    @Override
    public A decision(State<A> from) {
        List<StateAction<A>> maxStates = from.getActs()
                .map(act -> new StateAction<>(from, act))
                .collect(MaxUtil.maxList(Comparator.comparing(this::minValue)));
        return maxStates.get(ThreadLocalRandom.current().nextInt(maxStates.size())).getAction();
    }

    private GameResult maxValue(StateAction<A> stateAction) {
        State<A> state = stateAction.getResult();
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActs().map(act -> new StateAction<>(state, act))
                .map(this::minValue).max(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }

    private GameResult minValue(StateAction<A> stateAction) {
        State<A> state = stateAction.getResult();
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActs().map(act -> new StateAction<>(state, act))
                .map(this::maxValue).min(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }
}