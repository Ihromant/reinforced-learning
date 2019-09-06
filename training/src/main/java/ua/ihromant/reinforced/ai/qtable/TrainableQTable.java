package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;

import java.util.Map;

public interface TrainableQTable<A> extends QTable<A> {
    void set(StateAction<A> stateAction, double newValue);

    void setMultiple(Map<StateAction<A>, Double> newValues);

    void serialize(String path);
}
