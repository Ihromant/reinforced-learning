package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.state.State;

public interface TrainingAgent<A> extends Agent<A> {
    void train(State<A> baseState, int episodes, String target);
}
