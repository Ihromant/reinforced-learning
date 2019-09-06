package ua.ihromant.reinforced.ai.qtable.map;

import ua.ihromant.learning.qtable.MapQTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.reinforced.ai.qtable.TrainableQTable;

import java.util.HashMap;
import java.util.Map;

public class TrainableMapQTable<A> extends MapQTable<A> implements TrainableQTable<A> {
    private final double alpha;

    public TrainableMapQTable(double alpha) {
        super(new HashMap<>());
        this.alpha = alpha;
    }

    public TrainableMapQTable(Map<StateAction<A>, Double> qTable, double alpha) {
        super(qTable);
        this.alpha = alpha;
    }

    @Override
    public void set(StateAction<A> action, double newValue) {
        qStates.compute(action, (act, oldVal) -> {
            oldVal = oldVal != null ? oldVal : 0.0;
            return (1 - alpha) * oldVal + alpha * newValue;
        });
    }

    @Override
    public void setMultiple(Map<StateAction<A>, Double> newValues) {
        newValues.forEach(this::set);
    }

    @Override
    public void serialize(String path) {
        // TODO
    }
}

