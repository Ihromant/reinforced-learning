package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.Map;

public interface MonteCarloSearchThree<A> extends QTable<A> {
	Map<State<A>, Double> getTree();
}
