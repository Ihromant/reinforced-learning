package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.state.State;

import java.util.Map;

public interface TrainableQTable<A> extends QTable<A> {
    void set(State<A> state, double newValue);

    void setMultiple(Map<State<A>, Double> newValues);

    void serialize(String path);
}
