package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.State;

import java.util.Map;

public interface TrainableQTable<A> extends QTable<A> {
    void set(StateAction<State<A>, A> stateAction, double newValue);

    void setMultiple(Map<StateAction<State<A>, A>, Double> newValues);

    void serialize(String path);
}
