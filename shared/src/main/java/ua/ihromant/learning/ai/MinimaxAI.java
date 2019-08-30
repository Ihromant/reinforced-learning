package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
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
    public State<A> decision(State<A> from) {
        List<State<A>> maxStates = from.getStates().collect(MaxUtil.maxList(Comparator.comparing(this::minValue)));
        return maxStates.get(ThreadLocalRandom.current().nextInt(maxStates.size()));
    }

    private GameResult maxValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .map(this::minValue).max(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }

    private GameResult minValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .map(this::maxValue).min(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }
}