package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapQTableBackedTree<A> implements MonteCarloSearchTree<A> {
    private final Map<StateAction<A>, Double> qStates = new HashMap<>();
    private final QTable<A> backed;

    public MapQTableBackedTree(QTable<A> backed) {
        this.backed = backed;
    }

    @Override
    public double get(StateAction<A> action) {
        if (!qStates.containsKey(action)) {
            qStates.put(action, backed.get(action));
        }
        return qStates.get(action);
    }

    @Override
    public Map<StateAction<A>, Double> getMultiple(Stream<StateAction<A>> actions) {
        List<StateAction<A>> pairs = actions.collect(Collectors.toList());
        Map<StateAction<A>, Double> missingEvals = backed.getMultiple(pairs.stream().filter(st -> !qStates.containsKey(st)));
        Map<StateAction<A>, Double> existingEvals = pairs.stream().filter(qStates::containsKey)
                .collect(Collectors.toMap(Function.identity(), qStates::get));
        existingEvals.putAll(missingEvals);
        return existingEvals;
    }

    @Override
    public void set(StateAction<A> action, double newValue) {
        qStates.put(action, newValue);
    }

    @Override
    public void setMultiple(Map<StateAction<A>, Double> newValues) {
        qStates.putAll(newValues);
    }

    @Override
    public void serialize(String path) {
        // no need for it
    }

    @Override
    public Map<StateAction<A>, Double> getTree() {
        Map<StateAction<A>, Double> evaluations = backed.getMultiple(qStates.keySet().stream());
        return qStates.keySet().stream().filter(st -> !qStates.get(st).equals(evaluations.get(st)))
                .collect(Collectors.toMap(Function.identity(), qStates::get));
    }
}
