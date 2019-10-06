package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.qtable.StateAction;

import java.util.Map;

public interface MonteCarloSearchTree<A> extends TrainableQTable<A> {
    Map<StateAction<A>, Double> getTree();
}
