package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.Result;
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
    public Decision<A> decision(State<A> from, List<HistoryItem<A>> history) {
        List<StateAction<A>> maxStates = from.getActions()
                .map(act -> new StateAction<>(from, act))
                .collect(MaxUtil.maxList(Comparator.comparing(this::minValue)));
        return new Decision<>(maxStates.get(ThreadLocalRandom.current().nextInt(maxStates.size())).getAction());
    }

    private double maxValue(StateAction<A> stateAction) {
        State<A> state = stateAction.getResult();
        Result res = state.getResult();
        if (res != null) {
            return res.getUtility(player);
        }
        return state.getActions().map(act -> new StateAction<>(state, act))
                .map(this::minValue).max(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }

    private double minValue(StateAction<A> stateAction) {
        State<A> state = stateAction.getResult();
        Result res = state.getResult();
        if (res != null) {
            return res.getUtility(player);
        }
        return state.getActions().map(act -> new StateAction<>(state, act))
                .map(this::maxValue).min(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }
}