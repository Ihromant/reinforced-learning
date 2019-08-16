package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.agent.Agent;

public interface TrainingAgent<A> extends Agent<A> {
    void train(int episodes, String target);
}
